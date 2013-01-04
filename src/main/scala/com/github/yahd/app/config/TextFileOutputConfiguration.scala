package com.github.yahd.app.config

import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.output._

import parameter._

abstract class FileOuputConfiguration(pathParam: PathParameter) extends OutputConfiguration {
  def configureJob(cmdLine: Array[String], job: Job) {
    FileOutputFormat.setOutputPath(job, pathParam.toPath(cmdLine))
    configureFormat(cmdLine, job)
  }

  def configureFormat(cmdLine: Array[String], job: Job)
}

class TextFileOutputConfiguration(pathParam: PathParameter) extends FileOuputConfiguration(pathParam) {

  def configureFormat(cmdLine: Array[String], job: Job) =
    job.setOutputFormatClass(classOf[TextOutputFormat[_, _]])
}