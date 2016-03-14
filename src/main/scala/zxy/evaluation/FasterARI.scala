package zxy.evaluation

/**
  * Author: Xingyu Zhou
  * Email: xingyuhit@gmail.com
  */

class FasterARI(computedLabels: Array[Int], trueLabels: Array[Int])
  extends ARI(computedLabels, trueLabels) {

  def this(computedLabelPath: String, trueLabelPath: String) {
    this(ARI.labelsFromFile(computedLabelPath), ARI.labelsFromFile(trueLabelPath))
  }

  def this(computedLabelPath: String, trueLabels: Array[Int]) {
    this(ARI.labelsFromFile(computedLabelPath), trueLabels)
  }

  def this(computedLabels: Array[Int], trueLabelPath: String) {
    this(computedLabels, ARI.labelsFromFile(trueLabelPath))
  }

  override def adjustedRandIndex(): Double = AdjustedRandIndex.measure(computedLabels, trueLabels)
}
