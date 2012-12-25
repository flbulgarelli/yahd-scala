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
import com.github.yahd.app.config.FileInputType
import com.github.yahd.app.config.FileOutputType
import com.github.yahd.app.config.OutputType
import com.github.yahd.app.config.InputType
import com.github.yahd.app.config.JobConfiguration
import com.github.yahd.app.config.Fixed
import com.github.yahd.app.config.CommandLine

trait JobRunner {
  private var inputType: JobConfiguration = _
  private var outputType: JobConfiguration = _

  private val conf = new Configuration
  implicit val job = new Job(conf)

  private def configureInputType(inputType: InputType) = {
    this.inputType = inputType
    inputType
  }

  def fromInputPath = configureInputType(new FileInputType(CommandLine(0)))
  def fromInputPath(src: String) = configureInputType(new FileInputType(Fixed(src)))

  private def configureInputFormat(inputFormat: Class[_ <: InputFormat[_, _]]) {
    job.setInputFormatClass(inputFormat)
  }

  def parseText = {
    configureInputFormat(classOf[TextInputFormat])
    from[String]
  }

  private def configureOutputFormat(format: Class[_ <: OutputFormat[_, _]]) {
    job.setOutputFormatClass(format)
  }

  implicit def terminal2Formatteable[A, B, C, D, E](terminal: TerminalLike[A, B, C, D, E]) = new AnyRef {
    def formatText = {
      configureOutputFormat(classOf[TextOutputFormat[_, _]])
      terminal.mcr
    }
  }
  
  import state.Map._ 
  implicit def map2Formatteable[A, B, C](map: state.Map[A, (B, C)]) = terminal2Formatteable(map)
  
  private def configureOutputType(outputType: OutputType) = {
    this.outputType = outputType
    outputType
  }

  def toOutputPath = configureOutputType(new FileOutputType(CommandLine(1)))
  def toOutputPath(drain: String) = configureOutputType(new FileOutputType(Fixed(drain)))

  def runJob(jobName: String, args: Array[String]) {
    job.setJobName(jobName)
    job.setJarByClass(getClass)

    inputType.configureJob(args, job)
    outputType.configureJob(args, job)

    app.AppGlobalConfig.configureJob(job)
    
    job.submit
  }

}