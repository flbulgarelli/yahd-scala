name := "yahd-scala"

version := "1.0.0-SNAPSHOT"

organization := "net.sf.roggen"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq( 
 "org.apache.hadoop" % "hadoop-core" % "1.0.3",
 "org.apache.mrunit" % "mrunit" % "0.9.0-incubating" % "test" classifier "hadoop1",
 "org.scalatest" % "scalatest_2.9.1" % "1.8" % "test"
)
