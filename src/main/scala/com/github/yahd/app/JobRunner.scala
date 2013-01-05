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
import com.github.yahd.app.config.InputConfiguration

/**
 * Haddop Job configuration DSL entry point
 *
 * @author flbulgarelli
 */
trait JobRunner {

  type ConfigurationFunction = (Array[String], Job) => Unit

  class OnTheFlyConfiguration(f: ConfigurationFunction) extends JobConfiguration {
    override def apply(args: Array[String], job: Job) {
      f(args, job)
    }
  }

  def from[A] = new OnTheFlyConfiguration(_: ConfigurationFunction) with InputConfiguration[A]

  def to = new OnTheFlyConfiguration(_: ConfigurationFunction) with OutputConfiguration
  
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

  implicit def string2Path = new Path(_: String)

  def runJob(jobName: String, args: Array[String]) {
    jobFactory.createJob(jobName, args).submit
  }

}