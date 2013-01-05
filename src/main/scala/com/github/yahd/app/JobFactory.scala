package com.github.yahd.app
import config._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job

class JobFactory {
  private val conf = new Configuration
  private val job = new Job(conf)

  import scala.collection.mutable.ArrayBuffer
  private val configurations: ArrayBuffer[JobConfiguration] = new ArrayBuffer(3)

  def +=(configuration: JobConfiguration ) {
    configurations += configuration
  }


  def createJob(jobName: String, args: Array[String]) = {
    job.setJobName(jobName)
    job.setJarByClass(getClass)

    configurations.foreach { _.configureJob(args, job) }

    AppGlobalConfig.configureJob(job)
    job
  }
}