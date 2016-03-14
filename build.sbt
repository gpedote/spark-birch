name := "zxy-birch"

version := "1.0"

scalaVersion := "2.10.6"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.5.1",
  "org.apache.spark" %% "spark-mllib" % "1.5.1"
)

libraryDependencies += "com.github.scopt" %% "scopt" % "3.4.0"

resolvers += Resolver.sonatypeRepo("public")


import sbtassembly.AssemblyPlugin.autoImport._

assemblyJarName in assembly := "zbirch.jar"

test in assembly := {}

mainClass in assembly := Some("zxy.birch.BirchClustering")

assemblyMergeStrategy in assembly := {
  case PathList("javax", "xml", xs @ _*)         => MergeStrategy.first
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*)         => MergeStrategy.last
  case PathList("com", "google", xs @ _*)         => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}