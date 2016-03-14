package zxy.birch

import zxy.sizeof.SizeEstimator

/**
  * Author: Xingyu Zhou
  * Email: xingyuhit@gmail.com
  */

class CFTree(val nMaxBranch: Int = DefaultParam.MAX_BRANCH,
             var distThreshold: Double = DefaultParam.DIST_THRESHOLD)
  extends Iterable[CFEntry] with Serializable {

  var autoBuild: Boolean = DefaultParam.AUTO_BUILD
  var memLimit: Double = DefaultParam.MEM_LIMIT
  var memCheckPeriod: Int = DefaultParam.MEM_CHECK_PERIOD
  var tick: Int = 0

  var root: CFNode = new CFLeafNode(nMaxBranch, distThreshold)
  var leafDummyNode: CFLeafNode = new CFLeafNode(nMaxBranch, distThreshold)
  leafDummyNode.setNextLeaf(root.asInstanceOf[CFLeafNode])

  def enableAutoRebuild(memLimit: Double = this.memLimit, memCheckPeriod: Int = this.memCheckPeriod) {
    autoBuild = true
    this.memLimit = memLimit
    this.memCheckPeriod = memCheckPeriod
  }

  def disableAutoRebuild() {
    autoBuild = false
  }

  def insertEntry(entry: CFEntry) {
    val needSplit = root.insertEntry(entry)
    if (needSplit) {
      root.split().foreach {
        case (leftNode, rightNode) =>
          val newLeftEntry = new CFEntry(
            leftNode.entries.map(_.n).sum,
            leftNode.entries.map(_.ls).reduce(_ + _),
            leftNode.entries.map(_.ss).reduce(_ + _),
            leftNode
          )

          val newRightEntry = new CFEntry(
            rightNode.entries.map(_.n).sum,
            rightNode.entries.map(_.ls).reduce(_ + _),
            rightNode.entries.map(_.ss).reduce(_ + _),
            rightNode
          )

          val newRoot = new CFNode(nMaxBranch, distThreshold)
          newRoot.addEntry(newLeftEntry)
          newRoot.addEntry(newRightEntry)

          root = newRoot
          System.gc()
      }
    }

    tick += 1
    if (autoBuild && tick == memCheckPeriod) {
      tick = 0
      rebuildIfAboveMemLimit()
    }
  }

  def rebuildIfAboveMemLimit() {
    val curMemUsage = computeMemoryUsage

    if (curMemUsage <= memLimit) return

    val newDistThreshold = computeGreaterThreshold(distThreshold * (curMemUsage / memLimit))
    //    println(s"Memory Usage: $curMemUsage (limit is $memLimit)") // for debug!!
    //    println(s"distThreshold: $distThreshold -> $newDistThreshold") // for debug!!
    rebuildTree(newDistThreshold)
    //    println(s"Memory Usage (after rebuild): $computeMemoryUsage") // for debug!!
    //    println(s"Total Nodes so far: ${leafNodes.size}") // for debug!!
    //    println(s"Total Entries so far: ${leafEntries.size}") // for debug!!
    //    println(s"Total Points so far: ${leafEntries.map(_.n).sum}")  // for debug!!
  }

  def computeGreaterThreshold(minThreshold: Double = 0): Double = {
    var estimatedT = 0.0
    var count = 0

    leafNodes.foreach { node =>
      node.closestEntryPair.foreach { case (entry1, entry2) =>
        estimatedT += entry1.distTo(entry2)
        count += 1
      }
    }

    if (count > 0) estimatedT /= count

    val threshold = math.max(minThreshold, math.min(estimatedT, minThreshold * 2))

    if (threshold < distThreshold) distThreshold * 2
    else threshold
  }

  def computeMemoryUsage: Double = SizeEstimator.estimate(root)

  def rebuildTree(newDistThreshold: Double) {
    val newTree = new CFTree(nMaxBranch, newDistThreshold)
    newTree.autoBuild = autoBuild
    newTree.memLimit = memLimit
    newTree.memCheckPeriod = memCheckPeriod
    newTree.mergeTree(this)
    distThreshold = newDistThreshold
    root = newTree.root
    leafDummyNode = newTree.leafDummyNode
    System.gc()
  }

  def mergeTree(that: CFTree) {
    that.leafEntries.foreach(insertEntry)
  }

  override def iterator: Iterator[CFEntry] = leafEntries

  def leafEntries: Iterator[CFEntry] =
    for {
      node <- leafNodes
      e <- node.entries
    } yield e

  def leafNodes = new Iterator[CFNode] {
    var cur = leafDummyNode

    override def hasNext: Boolean = cur.nextLeaf != null

    override def next(): CFNode = {
      cur = cur.nextLeaf
      cur
    }
  }
}
