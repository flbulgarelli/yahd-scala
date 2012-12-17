package com.github.yahd.app.config
import com.github.yahd.MCR
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import com.github.yahd.Converter
import com.github.yahd._
import com.github.yahd.app.InputType
import com.github.yahd.app.PathParameter

class FileInputType(pathParam: PathParameter) extends InputType {
  def configureJob(cmdLine: Array[String], job: Job) =
    FileInputFormat.addInputPath(job, pathParam.toPath(cmdLine))
}
