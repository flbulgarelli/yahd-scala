package com.github.yahd.app.config
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.Job
import parameter._
class TextFileInputConfiguration(pathParam: PathParameter) extends FileInputConfiguration[String](pathParam) {
  override def configureFormat(cmdLine: Array[String], job: Job) =
    job.setInputFormatClass(classOf[TextInputFormat])
}
