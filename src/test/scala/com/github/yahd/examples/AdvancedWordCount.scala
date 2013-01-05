package com.github.yahd.examples
import com.github.yahd._
import Prelude._
import Yahd._
import app._
import com.github.yahd.app.config.InputConfiguration
import com.github.yahd.app.config.InputConfiguration
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import com.github.yahd.app.config.parameter.CommandLine
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat

object YahdWordGroupByLengthJob extends JobApp("word group by length") {
  val src = "src/test/resources/sample.txt"
  val dest = "out3"

  fromTextFile(src) >> { _.flatMap(_.words).map(x => (x.length, x)) } >> toTextFile(dest)
}

object YahdWordCountByLengthJob extends JobApp("word count by length") {
  from[String] { (args, job) =>
    FileInputFormat.addInputPath(job, args(0))
    job.setInputFormatClass(classOf[TextInputFormat])
  } >> {
    _.flatMap(_.words).groupOn(_.length).lengthValues
  } >> to { (args, job) =>
    FileOutputFormat.setOutputPath(job, "out")
    job.setOutputFormatClass(classOf[TextOutputFormat[_, _]])
  }
}


