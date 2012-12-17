package com.github.yahd.app.config
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.Job
import com.github.yahd.app.OutputType
import com.github.yahd.app.PathParameter

class FileOutputType(pathParam: PathParameter) extends OutputType {
  def configureJob(cmdLine: Array[String], job: Job) =
    FileOutputFormat.setOutputPath(job, pathParam.toPath(cmdLine))
}