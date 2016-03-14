package zxy.birch

/**
  * Author: Xingyu Zhou
  * Email: xingyuhit@gmail.com
  */

class Point(val value: Array[Double]) extends Serializable {
  def distTo(that: Point): Double = math.sqrt {
    (value zip that.value).map { case (x, y) =>
      val diff = x - y
      diff * diff
    }.sum
  }

  def +(that: Point): Point = new Point((value zip that.value).map { case (x, y) => x + y })

  def /(divisor: Double): Point = new Point(value.map(_ / divisor))

  override def toString: String = s"Point($value)"

  def apply(i: Int) = value(i)
}
