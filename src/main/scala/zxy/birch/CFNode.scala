package zxy.birch

import scala.collection.mutable

/**
  * Author: Xingyu Zhou
  * Email: xingyuhit@gmail.com
  */


class CFNode(val nMaxBranch: Int, val distThreshold: Double) extends Serializable {
  var entries: mutable.Set[CFEntry] = mutable.HashSet.empty[CFEntry]

  def addEntry(entry: CFEntry) {
    entries += entry
  }

  def removeEntry(entry: CFEntry) {
    entries -= entry
  }

  def closestEntryWith(entry: CFEntry): CFEntry =
    entries.minBy(e => e.distTo(entry))

  def closestEntryPair: Option[(CFEntry, CFEntry)] = {
    if (entries.size < 2) return None

    Option {
      {
        for {
          e1 <- entries
          e2 <- entries
          if e1.## < e2.##
        } yield (e1, e2)
      }.minBy { case (x, y) => x.distTo(y) }
    }
  }

  def farthestEntryPair: Option[(CFEntry, CFEntry)] = {
    if (entries.size < 2) return None

    Option {
      {
        for {
          e1 <- entries
          e2 <- entries
          if e1.## < e2.##
        } yield (e1, e2)
      }.maxBy { case (x, y) => x.distTo(y) }
    }
  }

  def split(): Option[(CFNode, CFNode)] =
    farthestEntryPair map {
      case (farLeft, farRight) =>
        val leftNode = new CFNode(nMaxBranch, distThreshold)
        val rightNode = new CFNode(nMaxBranch, distThreshold)
        for (entry <- entries) {
          if (entry.distTo(farLeft) < entry.distTo(farRight)) {
            leftNode.addEntry(entry)
          } else {
            rightNode.addEntry(entry)
          }
        }
        (leftNode, rightNode)
    }

  def insertEntry(entry: CFEntry): Boolean = {
    if (entries.isEmpty) {
      addEntry(entry)
      return false
    }

    val closestEntry = closestEntryWith(entry)

    if (closestEntry.child != null) {
      val needSplit = closestEntry.child.insertEntry(entry)
      if (needSplit) {
        closestEntry.child.split().foreach {
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

            removeEntry(closestEntry)
            addEntry(newLeftEntry)
            addEntry(newRightEntry)

            entries.size > nMaxBranch
        }
      }
      closestEntry.update(entry)
      false
    } else if (closestEntry.canMerge(entry, distThreshold)) {
      closestEntry.update(entry)
      false
    } else {
      addEntry(entry)
      entries.size > nMaxBranch
    }
  }
}

class CFLeafNode(nMaxBranch: Int, distThreshold: Double) extends CFNode(nMaxBranch, distThreshold) {
  var nextLeaf: CFLeafNode = null
  var prevLeaf: CFLeafNode = null

  override def split(): Option[(CFLeafNode, CFLeafNode)] =
    farthestEntryPair map {
      case (farLeft, farRight) =>
        val leftNode = new CFLeafNode(nMaxBranch, distThreshold)
        val rightNode = new CFLeafNode(nMaxBranch, distThreshold)

        for (entry <- entries) {
          if (entry.distTo(farLeft) < entry.distTo(farRight)) {
            leftNode.addEntry(entry)
          } else {
            rightNode.addEntry(entry)
          }
        }

        leftNode.setNextLeaf(rightNode)
        if (prevLeaf != null) prevLeaf.setNextLeaf(leftNode)
        if (nextLeaf != null) rightNode.setNextLeaf(nextLeaf)

        (leftNode, rightNode)
    }

  def setNextLeaf(node: CFLeafNode) {
    nextLeaf = node
    if (node != null) {
      node.prevLeaf = this
    }
  }
}
