package com.github.yahd.app

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job
import com.github.yahd._
import Yahd._
import builder._
import org.apache.hadoop.mapred.FileOutputFormat
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.fs.Path
import com.github.yahd.builder.state.TerminalLike
import com.github.yahd.Prelude._
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.InputFormat
import org.apache.hadoop.mapreduce.OutputFormat
import com.github.yahd.app.config.TextFileInputConfiguration
import com.github.yahd.app.config.TextFileOutputConfiguration
import com.github.yahd.app.config.OutputConfiguration
import com.github.yahd.app.config.InputConfiguration
import com.github.yahd.app.config.JobConfiguration
import com.github.yahd.app.config.parameter._

/**
 * Haddop Job configuration DSL entry point
 *
 * @author flbulgarelli
 */
trait JobRunner {

  def fromTextFile: TextFileInputConfiguration =
    fromTextFile(CommandLine(0))

  def fromTextFile(src: String): TextFileInputConfiguration =
    fromTextFile(Fixed(src))

  def fromTextFile(path: PathParameter) =
    new TextFileInputConfiguration(path)

  def toTextFile: TextFileOutputConfiguration =
    toTextFile(CommandLine(1))

  def toTextFile(drain: String): TextFileOutputConfiguration =
    toTextFile(Fixed(drain))

  def toTextFile(path: PathParameter): TextFileOutputConfiguration =
    new TextFileOutputConfiguration(path)

  implicit val jobFactory: JobFactory = new JobFactory

  def runJob(jobName: String, args: Array[String]) {
    jobFactory.createJob(jobName, args).submit
  }

}