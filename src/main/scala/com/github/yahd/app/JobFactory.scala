package com.github.yahd.app
import config._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job

class JobFactory {

  import scala.collection.mutable.ArrayBuffer
  private val configurations: ArrayBuffer[JobConfiguration] = new ArrayBuffer(3)

  /**Adds a [[JobConfiguration]] to this factory */
  def +=(configuration: JobConfiguration) {
    configurations += configuration
  }

  /**
   * Creates a new [[org.apache.hadoop.mapreduce.Job]], sets its jar class,
   * its job name, and applies each configuration in this factory to it
   * 
   * @return the new job
   */
  def createJob(jobName: String, args: Array[String]) = {
    val conf = new Configuration
    val job = new Job(conf)
    job.setJobName(jobName)
    job.setJarByClass(getClass)

    configurations.foreach { _.configureJob(args, job) }

    AppGlobalConfig.configureJob(job)
    job
  }
}