package zxy.evaluation

import java.io.File

import scala.collection.mutable
import scala.io.Source

/**
  * Author: Xingyu Zhou
  * Email: xingyuhit@gmail.com
  *
  * Support single file or directory (spark style)
  */

class ARI(computedLabels: Array[Int], trueLabels: Array[Int]) {

  def this(computedLabelPath: String, trueLabelPath: String) {
    this(ARI.labelsFromFile(computedLabelPath), ARI.labelsFromFile(trueLabelPath))
  }

  def this(computedLabelPath: String, trueLabels: Array[Int]) {
    this(ARI.labelsFromFile(computedLabelPath), trueLabels)
  }

  def this(computedLabels: Array[Int], trueLabelPath: String) {
    this(computedLabels, ARI.labelsFromFile(trueLabelPath))
  }

  protected def getStats: (Long, Long, Long, Long) = {
    require(computedLabels.length == trueLabels.length,
      s"computed:${computedLabels.length} truth:${trueLabels.length}")

    assert(computedLabels.length > 0)

    var a: Long = 0 // # of pairs with same labels in both computed and groundTruth
    var b: Long = 0 // # of pairs with same labels in computed but different in groundTruth
    var c: Long = 0 // # of pairs with same labels in groundTruth but different in computed
    var d: Long = 0 // # of pairs with different labels in both computed and groundTruth

    val size = computedLabels.length
    for (i <- 0 until size) {
      val c1 = computedLabels(i)
      val t1 = trueLabels(i)

      for (j <- i + 1 until size) {
        val c2 = computedLabels(j)
        val t2 = trueLabels(j)

        if (c1 == c2 && t1 == t2) {
          a += 1
        } else if (c1 == c2 && t1 != t2) {
          b += 1
        } else if (c1 != c2 && t1 == t2) {
          c += 1
        } else if (c1 != c2 && t1 != t2) {
          d += 1
        }
      }
    }

    (a, b, c, d)
  }

  def randIndex(): Double = {
    val (a: Long, b: Long, c: Long, d: Long) = getStats
    (a + d).toDouble / (a + b + c + d)
  }

  def adjustedRandIndex(): Double = {
    val (a: Long, b: Long, c: Long, d: Long) = getStats
    val n = a + b + c + d
    val tmp: Long = (a + b) * (a + c) + (c + d) * (b + d)
    (n * (a + d) - tmp).toDouble / (n * n - tmp)
  }
}

object ARI {
  val pattern = "part-%05d"

  def labelsFromFile(path: String): Array[Int] = {
    val d = new File(path)

    require(d.exists(), s"'$path' does not exist")

    val labels: mutable.ListBuffer[Int] = mutable.ListBuffer[Int]()

    if (d.isDirectory) {
      // use part-00001, part-00002, ...
      var i = 0
      var file = new File(d.getAbsolutePath + File.separator + pattern.format(i))
      while (file.exists()) {
        Source.fromFile(file).getLines.map(_.toInt).foreach(labels.append(_))
        i += 1
        file = new File(d.getAbsolutePath + File.separator + pattern.format(i))
      }
    } else {
      Source.fromFile(path).getLines.map(_.toInt).foreach(labels.append(_))
    }

    labels.toArray
  }
}
