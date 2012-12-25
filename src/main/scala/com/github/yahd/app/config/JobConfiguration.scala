package com.github.yahd.app.config
import org.apache.hadoop.mapreduce.Job

trait JobConfiguration {
  def configureJob(cmdLine: Array[String], job: Job)
}
