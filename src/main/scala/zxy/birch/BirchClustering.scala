package zxy.birch


import org.apache.log4j.{FileAppender, Level, Logger, PatternLayout}
import org.apache.spark.{SparkConf, SparkContext}
import scopt.OptionParser
import zxy.evaluation.FasterARI

/**
  * Author: Xingyu Zhou
  * Email: xingyuhit@gmail.com
  */


object BirchClustering {
  def main(args: Array[String]) {

    val parser = new OptionParser[Config]("zbirch") {
      opt[String]("master") valueName "<master>" action { (x, c) =>
        c.copy(master = x)
      } text "sc.setMaster(master)"

      opt[String]("data") required() valueName "<file>" action { (x, c) =>
        c.copy(dataFile = x)
      } text "input data file"

      opt[String]("labels") required() valueName "<file>" action { (x, c) =>
        c.copy(labelFile = x)
      } text "input labels file"

      opt[String]("res") valueName "<file>" action { (x, c) =>
        c.copy(resFile = x)
      } text "result label file"

      opt[String]("log") valueName "<file>" action { (x, c) =>
        c.copy(logFile = x)
      } text "log file"

      opt[Int]("pars") valueName "<nPartitions>" action { (x, c) =>
        c.copy(nPartitions = x)
      } text "number of partitions"

      opt[Int]("k") required() valueName "<nClusters>" action { (x, c) =>
        c.copy(nClusters = x)
      } text "number of clusters"

      opt[Int]("iters") valueName "<nIterations>" action { (x, c) =>
        c.copy(nIterations = x)
      } text "number of max iterations"

      opt[Int]("memory") valueName "<memLimit>" action { (x, c) =>
        c.copy(memLimit = x)
      } text "memory limit reference"

      opt[Int]("checkperiod") valueName "<memCheckPeriod>" action { (x, c) =>
        c.copy(memCheckPeriod = x)
      } text "memory checking period"
    }

    val conf = new SparkConf().setAppName("Birch Clustering App")

    var nClusters: Int = -1
    var nPartitions: Int = -1
    var memLimit = -1
    var memCheckPeriod = -1
    var nIterations: Int = -1
    var dataFile: String = ""
    var labelFile: String = ""
    var resFile: String = ""
    var logFile: String = ""

    // parser.parse returns Option[C]
    parser.parse(args, Config()) match {
      case Some(config) =>
        dataFile = config.dataFile
        labelFile = config.labelFile
        nClusters = config.nClusters
        if (!config.master.isEmpty) conf.setMaster(config.master)
        logFile = if (!config.logFile.isEmpty) config.logFile else dataFile.replace("data", "log")
        resFile = if (!config.resFile.isEmpty) config.resFile else dataFile.replace("data", "res")
        nPartitions = if (config.nPartitions > 0) config.nPartitions else 4
        nIterations = if (config.nIterations > 0) config.nIterations else 20
        memLimit = if (config.memLimit > 0) config.memLimit else DefaultParam.MEM_LIMIT
        memCheckPeriod = if (config.memCheckPeriod > 0) config.memCheckPeriod else DefaultParam.MEM_CHECK_PERIOD

      case None =>
      // arguments are bad, error message will have been displayed
    }

    val sc = new SparkContext(conf)
    //    sc.setLogLevel("WARN")

    val logger = Logger.getLogger("zbirch")
    logger.addAppender(new FileAppender(new PatternLayout("%d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n"), logFile, false))
    logger.setLevel(Level.ALL)

    val lines = sc.textFile(dataFile, nPartitions)
    val data = lines.map(line => new Point(line.trim.split("\\s+|,").map(_.toDouble))).persist()
    logger.info(s"Number of partitions: ${data.partitions.length}\n") // just make it happen instead of delay

    val birch = new AdvancedBirch(nClusters)
    birch.train(data, nIterations, memLimit, memCheckPeriod)

    val computedLabelsRDD = birch.predict(data)
    computedLabelsRDD.saveAsTextFile(resFile)

    // val trueLabels = sc.textFile(labelFile).map(_.toInt).collect()
    // val ari = new FasterARI(computedLabelsRDD.collect(), trueLabels).adjustedRandIndex()
    // logger.info(f"Adjusted Rand Index: $ari%f\n")
  }
}
