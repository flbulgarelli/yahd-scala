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
import com.github.yahd.app.config.Fixed
import com.github.yahd.app.config.CommandLine

class JobFactory {
  private val conf = new Configuration
  private val job = new Job(conf)

  import scala.collection.mutable.ArrayBuffer
  private val configurations: ArrayBuffer[JobConfiguration] = new ArrayBuffer(3)

  def +=(configuration: JobConfiguration ) = {
    configurations += configuration
  }


  def createJob(jobName: String, args: Array[String]) = {
    job.setJobName(jobName)
    job.setJarByClass(getClass)

    configurations.foreach { _.configureJob(args, job) }

    app.AppGlobalConfig.configureJob(job)
    job
  }

}

trait JobRunner {

  def fromTextFile = new TextFileInputConfiguration(CommandLine(0))
  def fromTextFile(src: String) = new TextFileInputConfiguration(Fixed(src))

  def toTextFile = new TextFileOutputConfiguration(CommandLine(1))
  def toTextFile(drain: String) = new TextFileOutputConfiguration(Fixed(drain))

  implicit val jobFactory: JobFactory = new JobFactory

  def runJob(jobName: String, args: Array[String]) {
    jobFactory.createJob(jobName, args).submit
  }

}