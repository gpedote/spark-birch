package zxy.birch

/**
  * Author: Xingyu Zhou
  * Email: xingyuhit@gmail.com
  */


class CFEntry(var n: Int, var ls: Point, var ss: Point, var child: CFNode) extends Serializable {
  def update(that: CFEntry) {
    ls += that.ls
    ss += that.ss
    n += that.n
  }

  def canMerge(that: CFEntry, distThreshold: Double): Boolean = distTo(that) <= distThreshold

  def distTo(that: CFEntry, which: Int = CFEntry.defaultDistanceFunction): Double = which match {
    case 0 => euclideanDistTo(that)
    case 2 => averageInterDistTo(that)
    case _ => throw new Exception("Unspported distance method")
  }

  def euclideanDistTo(that: CFEntry) = centroid.distTo(that.centroid)

  def centroid: Point = ls / n

  def averageInterDistTo(that: CFEntry) = {
    var dist = 0.0
    for (i <- ls.value.indices) {
      val diff = (that.n * ss(i) - 2 * ls(i) * that.ls(i) + n * that.ss(i)) / (n * that.n)
      dist += diff
    }
    math.sqrt(dist)
  }
}

object CFEntry {
  val defaultDistanceFunction: Int = 2

  def apply(point: Point) =
    new CFEntry(1, point, new Point(point.value.map(x => x * x)), null)
}
