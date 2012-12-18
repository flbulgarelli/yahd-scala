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
import builder.state.Initial
import builder.state.TerminalLike
import builder.state.Map._

object YahdWordCountJob extends JobApp("word count 1") {

  val src = "src/test/resources/sample.txt"
  val dest = "out"

  fromInputPath(src) >> parseText.concatMap(_.words).groupMapping(const(1)).combineSum.formatText >> toOutputPath(dest)
}

object YahdWordCountJob2 extends JobApp("word count 2") {

  val src = "src/test/resources/sample.txt"
  val dest = "out2"

  fromInputPath(src) >> parseText.concatMap(_.words).group.combineLength.formatText >> toOutputPath(dest)
}

object YahdWordGroupByLengthJob extends JobApp("word group by length") {

  val src = "src/test/resources/sample.txt"
  val dest = "out3"

  fromInputPath(src) >> parseText.concatMap(_.words).map(x => (x.length, x)).formatText >> toOutputPath(dest)
  
}