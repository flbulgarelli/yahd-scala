package com.github.yahd

import scala.Array.fallbackCanBuildFrom
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import com.github.yahd.Yahd.string2WordsOps
import Prelude.const
import Prelude.id
import org.scalatest.junit.JUnitRunner
import Yahd._
import Prelude.Grouping._
@RunWith(classOf[JUnitRunner])
class YahdTest extends FunSuite with YahdTestLike {

  val countsWords: YahdDriverAssertion = { it =>
    it.withInput(new WLong(), "hello world hello hello": WString)
    it.withInput(new WLong(), "world world world !": WString)
    it.withOutput("!": WString, 1: WInt)
    it.withOutput("hello": WString, 3: WInt)
    it.withOutput("world": WString, 4: WInt)
  }

  test("dsl with group") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .group
        .lengthValues
    }.testThat(countsWords)
  }

  test("dsl with mapWithKey") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .group
        .mapWithKey((x, y) => y.size)
    }.testThat(countsWords)
  }

  test("dsl with mapValuesUsingMapper") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .group
        .mapValuesUsingMapper(const(1))
        .sumValues
    }.testThat(countsWords)
  }

  test("dsl with mapValues") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .group.mapValues(const(1))
        .sumValues
    }.testThat(countsWords)
  }

  test("dsl with groupOn") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .groupOn(id)
        .lengthValues
    }.testThat(countsWords)
  }
  test("dsl with lengthValues") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .group
        .lengthValues
    }.testThat(countsWords)
  }

  test("dsl with groupMapping and sumValues") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .groupMapping(const(1))
        .sumValues
    }.testThat(countsWords)
  }

  test("dsl with groupMappingOn and mapValuesReducing") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .groupMappingOn(id)(const(1))
        .reduceValues(_ + _)
    }.testThat(countsWords)
  }

  test("dsl with groupMappingOn and combine ") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .groupMappingOn(id)(const(1))
        .reduceValuesUsingCombiner(_ + _)
    }.testThat(countsWords)
  }
  import Prelude.Grouping.onValue
  test("dsl with mapValues -- not combinable") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .group
        .mapGroup(onValue(_.size))
    }.testThat(countsWords)
  }

  test("dsl with map") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .map { x => (x, 1) }
        .groupMappingOn(_._1)(_._2)
        .mapGroup((k, vs) => (k, vs.sum))
    }.testThat(countsWords)
  }

  ignore("dsl without reducer") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .map { x => (x, 1) }
    }.testThat(countsWords)
  }

  test("dsl with filter") {
    defineJob { stream =>
      stream
        .filter(!_.startsWith("#"))
        .flatMap(_.words)
        .group
        .lengthValues
    }.testThat({ it =>
      it.withInput(new WLong(), "# hello hadoop": WString)
      it.withInput(new WLong(), "hello world hello hello": WString)
      it.withInput(new WLong(), "world world world !": WString)
      it.withOutput("!": WString, 1: WInt)
      it.withOutput("hello": WString, 3: WInt)
      it.withOutput("world": WString, 4: WInt)
    })
  }

  test("dsl with filter 2") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .filter(_.charAt(0).isLetterOrDigit)
        .group
        .lengthValues
    }.testThat { it =>
      it.withInput(new WLong(), "# hello hadoop": WString)
      it.withInput(new WLong(), "hello world hello hello": WString)
      it.withInput(new WLong(), "world world world !": WString)
      it.withOutput("hadoop": WString, 1: WInt)
      it.withOutput("hello": WString, 4: WInt)
      it.withOutput("world": WString, 4: WInt)
    }
  }

  test("dsl with filter 3") {
    defineJob { stream =>
      stream
        .flatMap(_.words)
        .group
        .lengthValues
        .filter(_ <= 3)
    }.testThat { it =>
      it.withInput(new WLong(), "hadoop": WString)
      it.withInput(new WLong(), "hello world hello hello": WString)
      it.withInput(new WLong(), "world world world": WString)
      it.withOutput("hadoop": WString, 1: WInt)
      it.withOutput("hello": WString, 3: WInt)
    }
  }


  test("AllWithDslLowLevelAndCombiner") {
    defineJob {
      MC[String, String, Int, String, Int](
        _.words.map { x => (x, 1) },
        (k, v) => (k, v.sum))
    }.testThat(countsWords)
  }

  test("AllWithDslLowLevel") {
    defineJob {
      MR[String, String, Int, String, Int](
        _.words.map { x => (x, 1) },
        (k, v) => List((k, v.sum)))
    }.testThat(countsWords)
  }

}
