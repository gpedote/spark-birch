package zxy.birch

import org.apache.spark.rdd.RDD

/**
  * Author: Xingyu Zhou
  * Email: xingyuhit@gmail.com
  */

class MyKMeans(val k: Int, var clusterCentroids: List[(Int, Point)] = null) extends Serializable {
  def train(data: RDD[Point], maxIterations: Int = 10) {
    if (clusterCentroids == null) {
      clusterCentroids = data.takeSample(false, k).zipWithIndex.map(_.swap).toList
    }

    for (iter <- 1 to maxIterations) {
      val dataWithClusterIndex = assignCluster(data)
      val newClusterCentroids = getCentroids(dataWithClusterIndex)
      if (newClusterCentroids == clusterCentroids) return
      clusterCentroids = newClusterCentroids
    }
  }

  def assignCluster(data: RDD[Point]): RDD[(Int, Point)] = {
    assume(clusterCentroids != null, "It needs to be trained first")

    val pointsWithDistances = data.map { point =>
      clusterCentroids.map { case (clusterIdx, centroid) =>
        (clusterIdx, point.distTo(centroid), centroid)
      }
    }

    pointsWithDistances
      .map(distances => distances.minBy(_._2))
      .map { case (clusterIdx, _, point) => (clusterIdx, point) }
  }

  def getCentroids(dataWithClusterIndex: RDD[(Int, Point)]): List[(Int, Point)] =
    dataWithClusterIndex
      .mapValues((_, 1))
      .reduceByKey { case ((pt1: Point, cnt1), (pt2: Point, cnt2)) => (pt1 + pt2, cnt1 + cnt2) }
      .mapValues { case (pt: Point, cnt) => pt / cnt }
      .collect()
      .toList

  def predict(data: RDD[Point]): RDD[Int] =
    assignCluster(data).map { case (clusterIdx, point) => clusterIdx }
}