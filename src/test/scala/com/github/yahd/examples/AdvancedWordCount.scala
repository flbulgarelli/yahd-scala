package com.github.yahd.examples
import com.github.yahd._
import Prelude._
import Yahd._
import app._

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


