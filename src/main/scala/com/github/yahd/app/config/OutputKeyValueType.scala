package com.github.yahd.app.config
import com.github.yahd.app
import org.apache.hadoop.mapreduce.Job
import com.github.yahd.app.JobConfiguration
import com.github.yahd.app.OutputType
import com.github.yahd.app.JobConfiguration

class OutputKeyValueType(ouputKeyClass: Class[_], ouputValueClass: Class[_]) {
  def >>(out: OutputType)(implicit job: Job) {
    job.setOutputKeyClass(ouputKeyClass)
    job.setOutputValueClass(ouputValueClass)
  }
}