package com.github.yahd.app.config.parameter

import org.apache.hadoop.fs.Path
import com.github.yahd.Prelude._

trait PathParameter {
  def value(cmdLine: Array[String]): String
  def toPath = (value _) >>> (new Path(_))
}
case class Fixed(value: String) extends PathParameter {
  def value(cmdLine: Array[String]) = value
}
case class CommandLine(pos: Int) extends PathParameter {
  def value(cmdLine: Array[String]) = cmdLine(pos)
}