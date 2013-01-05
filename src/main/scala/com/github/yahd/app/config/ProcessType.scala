package com.github.yahd.app.config
import org.apache.hadoop.mapreduce.Job
import com.github.yahd.app.JobFactory

class ProcessType(
  mapOutputKeyClass: Class[_],
  mapOutputValueClass: Class[_],
  outputKeyClass: Class[_],
  outputValueClass: Class[_]) extends JobConfiguration {

  override def apply(cmdLine: Array[String], job: Job) {
    job.setMapOutputKeyClass(mapOutputKeyClass)
    job.setMapOutputValueClass(mapOutputValueClass)
    job.setOutputKeyClass(outputKeyClass)
    job.setOutputValueClass(outputValueClass)
  }

  final def >>(out: OutputConfiguration)(implicit jobFactory: JobFactory) {
    jobFactory += this
    jobFactory += out
  }

}