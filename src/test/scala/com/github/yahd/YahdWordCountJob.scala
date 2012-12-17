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

object YahdWordCountJob extends JobApp("my job") {

  val src = "src/test/resources/sample.txt"
  val dest = "out"

  fromInputPath(src) >> parseText.concatMap(_.words).groupMapping(const(1)).combineSum.formatText >> toOutputPath(dest)
}