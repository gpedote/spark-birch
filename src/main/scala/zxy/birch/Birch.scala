package zxy.birch

import org.apache.log4j.Logger
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD

/**
  * Author: Xingyu Zhou
  * Email: xingyuhit@gmail.com
  */


class Birch(val k: Int = 0, nMaxBranch: Int = DefaultParam.MAX_BRANCH) extends Serializable {
  var kmeans: MyKMeans = null

  def train(data: RDD[Point],
            maxIterations: Int = 20,
            memLimit: Double = DefaultParam.MEM_LIMIT,
            memCheckPeriod: Int = DefaultParam.MEM_CHECK_PERIOD) {

    val logger = Logger.getLogger("zbirch")

    var totalDuration = 0.0

    var startTick = System.currentTimeMillis()
    logger.info(s"* Phase 1 started: $startTick")

    val leafEntryCentroids = data.mapPartitions { points =>
      val cfTree = new CFTree(nMaxBranch)
      cfTree.enableAutoRebuild(memLimit, memCheckPeriod)
      points.foreach(point => cfTree.insertEntry(CFEntry(point)))
      cfTree.iterator
    }.map(_.centroid).persist()

    val numLeafEntries = leafEntryCentroids.count() // just make it happen instead of delay
    logger.info(s" * Number of leaf entries: $numLeafEntries")

    var endTick = System.currentTimeMillis()
    var duration = (endTick - startTick) / 1000.0
    totalDuration += duration
    logger.info(f"* Phase 1 ended at ${endTick / 1000.0}%.3f")
    logger.info(f"** Time elapse on phase 1 is $duration%.2f sec **\n")

    startTick = System.currentTimeMillis()
    logger.info(f"* Phase 3 started at ${startTick / 1000.0}%.3f")

    if (k > 0 && k < numLeafEntries) {
      kmeans = new MyKMeans(k)
      kmeans.train(leafEntryCentroids, maxIterations)
    } else {
      kmeans = new MyKMeans(k, leafEntryCentroids.collect().zipWithIndex.map(_.swap).toList)
    }

    endTick = System.currentTimeMillis()
    duration = (endTick - startTick) / 1000.0
    totalDuration += duration
    logger.info(f"* Phase 3 ended at ${endTick / 1000.0}%.3f")
    logger.info(f" * Within Set Sum of Squared Error = ${computeCost(data)}%.2f")
    logger.info(f"** Time elapse on phase 3 is $duration%.2f sec **\n")

    startTick = System.currentTimeMillis()
    logger.info(f"* Phase 4 started at ${startTick / 1000.0}%.3f")

    kmeans = new MyKMeans(k, kmeans.clusterCentroids)
    kmeans.train(data, maxIterations)

    endTick = System.currentTimeMillis()
    duration = (endTick - startTick) / 1000.0
    totalDuration += duration
    logger.info(f"* Phase 4 ended at ${endTick / 1000.0}%.3f")
    logger.info(f" * Within Set Sum of Squared Error = ${computeCost(data)}%.2f")
    logger.info(f"** Time elapse on phase 4 is $duration%.2f sec **\n")

    logger.info(f"*** Time elapse in total is $totalDuration%.2f sec ***\n")
  }

  def computeCost(data: RDD[Point]): Double = {
    val vecData = data.map(p => Vectors.dense(p.value))
    val vecLeafEntryCentroids = clusterCentroids.map { case (i, p) =>
      Vectors.dense(p.value)
    }.toArray
    new KMeansModel(vecLeafEntryCentroids).computeCost(vecData)
  }

  def clusterCentroids: List[(Int, Point)] = kmeans.clusterCentroids

  def predict(data: RDD[Point]): RDD[Int] = {
    kmeans.predict(data)
  }
}
