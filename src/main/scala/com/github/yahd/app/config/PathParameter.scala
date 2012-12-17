package com.github.yahd.app
import org.apache.hadoop.fs.Path

trait PathParameter {
  def value(cmdLine: Array[String]): String
  def toPath = (value _).andThen(new Path(_))
}
case class Fixed(value: String) extends PathParameter {
  def value(cmdLine: Array[String]) = value
}
case class CommandLine(pos: Int) extends PathParameter {
  def value(cmdLine: Array[String]) = cmdLine(pos)
}