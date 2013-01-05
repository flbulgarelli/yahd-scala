package com.github.yahd.examples
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import com.github.yahd.Yahd.string2WordsOps
import com.github.yahd._
import Prelude._
import Yahd._
import Yahd.from
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import builder.state.Initial
import builder.state.TerminalLike
import builder.state.Map._
import app.JobApp


/**The simplest word count that can be described with Yahd*/
object WordCountJob extends JobApp("Deadly Simple Word Count") {

  val src = "src/test/resources/sample.txt"
  val dest = "out"

  fromTextFile(src) >> { _.flatMap(_.words).group.lengthValues } >> toTextFile(dest)
}

