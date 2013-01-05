package com.github.yahd.examples

import com.github.yahd._
import Yahd._
import Prelude._
import app._

object LowLevelApiWordCount extends JobApp("LowLevelWordCount") {

  val src = "src/test/resources/sample.txt"
  val dest = "out5"

  fromTextFile(src) >> { stream =>
    stream
      .m { x => x.words.flatMap { w => List((w, 1)) } }
      .c { (k, vs) => (k, vs.sum) }
  } >> toTextFile(dest)
}
