package com.github.yahd.app.config.parameter

import org.apache.hadoop.fs.Path
import com.github.yahd.Prelude._

/**
 * A Yahd Job path parameter
 * @author flbulgarelli
 */
trait PathParameter {
  def value(cmdLine: Array[String]): String
  def toPath = (value _) >>> (new Path(_))
}
/**Parameter that has a fixed value*/
case class Fixed(value: String) extends PathParameter {
  def value(cmdLine: Array[String]) = value
}
/**
 * Parameter that is provided as a command line argument
 * @param pos the index of the parameter in the arguments arrays
 */
case class CommandLine(pos: Int) extends PathParameter {
  def value(cmdLine: Array[String]) = cmdLine(pos)
}