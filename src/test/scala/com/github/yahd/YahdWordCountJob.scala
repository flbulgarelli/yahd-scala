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

  def removePunctuation(s: String) = s.filter { s => s.isSpaceChar || s.isLetterOrDigit }

  fromTextFile(src) >> { _.map(removePunctuation).flatMap(_.words).groupMapping(const(1)).sumValues } >> toTextFile(dest)
}

object YahdWordCountJob2 extends JobApp("word count 2") {

  val src = "src/test/resources/sample.txt"
  val dest = "out2"

  fromTextFile(src) >> { _.flatMap(_.words).group.lengthValues } >> toTextFile(dest)
}

object YahdWordCountJobPlain extends JobApp("word count 2") {

  val src = "src/test/resources/sample.txt"
  val dest = "out5"

  import Grouping._
  fromTextFile(src) >> MC[String, String, Int, String, Int](x => List((x, 1)), (k, vs) => (k, vs.sum)) >> toTextFile(dest)
}

object YahdWordCountJobPlain2 extends JobApp("word count 2") {

  val src = "src/test/resources/sample.txt"
  val dest = "out5"

  import Grouping._
  fromTextFile(src) >> { _.m { x => List((x, 1)) }.c { (k, vs) => (k, vs.sum) } } >> toTextFile(dest)
}

object YahdWordGroupByLengthJob extends JobApp("word group by length") {

  val src = "src/test/resources/sample.txt"
  val dest = "out3"

  fromTextFile(src) >> { _.flatMap(_.words).map(x => (x.length, x)) } >> toTextFile(dest)

}

object YahdWordCountByLengthJob extends JobApp("word count by length") {

  val src = "src/test/resources/sample.txt"
  val dest = "out4"

  fromTextFile(src) >> { _.flatMap(_.words).groupOn(_.length).lengthValues } >> toTextFile(dest)

}


