package com.github.yahd
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import com.github.yahd.Yahd.string2WordsOps
import app._
import Prelude._
import Yahd._
import Yahd.from
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat

object YahdWordCountJob extends App with YahdJob {

  defineJob { 
    from[String].concatMap(_.words).groupMapping(const(1)).combineSum 
  }

  job.setJobName("my job")
  
  job.setOutputKeyClass(classOf[Text])
  job.setOutputValueClass(classOf[IntWritable])
  
  job.setInputFormatClass(classOf[TextInputFormat])
  job.setOutputFormatClass(classOf[TextOutputFormat[_, _]])

  FileInputFormat.addInputPath(job, new Path("src/test/resources/sample.txt"))
  FileOutputFormat.setOutputPath(job, new Path("out.txt"))

  job.submit

}