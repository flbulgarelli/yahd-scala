package com.github.yahd.app.config
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.Job
import parameter._
abstract class FileInputConfiguration[A](pathParam: PathParameter) extends InputConfiguration[A] {
  def configureJob(cmdLine: Array[String], job: Job) {
    FileInputFormat.addInputPath(job, pathParam.toPath(cmdLine))
    configureFormat(cmdLine, job)
  }

  def configureFormat(cmdLine: Array[String], job: Job)
}