package com.github.yahd

import scala.Array.fallbackCanBuildFrom
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import com.github.yahd.Yahd.string2WordsOps
import Prelude.const
import Prelude.id
import org.scalatest.junit.JUnitRunner
import Yahd._

@RunWith(classOf[JUnitRunner])
class YahdTest extends FunSuite with YahdTestLike {

  test("dsl with group") {
    runStreamJob {
      _.
        flatMap(_.words).
        group.
        mapValuesLength
    }
  }
  test("dsl with groupOn") {
    runStreamJob {
      _.
        flatMap(_.words).
        groupOn(id).
        mapValuesLength
    }
  }
  test("dsl with combineLength") {
    runStreamJob {
      _.
        flatMap(_.words).
        group.
        combineLength
    }
  }

  test("dsl with groupMapping and combineSum") {
    runStreamJob {
      _.
        flatMap(_.words).
        groupMapping(const(1)).
        combineSum
    }
  }

  test("dsl with groupMappingOn and mapValuesReducing") {
    runStreamJob {
      _.
        flatMap(_.words).
        groupMappingOn(id)(const(1)).
        mapValuesReducing(_ + _)
    }
  }

  test("dsl with groupMappingOn and combine ") {
    runStreamJob {
      _.
        flatMap(_.words).
        groupMappingOn(id)(const(1)).
        combine(_ + _)
    }
  }
  test("dsl with mapValues -- not combinable") {
    runStreamJob {
      _.
        flatMap(_.words).
        group.
        mapValues(_.size)
    }
  }

  test("dsl with map") {
    runStreamJob {
      _.
        flatMap(_.words).
        map { x => (x, 1) }.
        groupMappingOn(_._1)(_._2).
        mapValues(_.sum)
    }
  }

  ignore("dsl without reducer") {
    runStreamJob {
      _.
        flatMap(_.words).
        map { x => (x, 1) }
    }
  }
  
  test("dsl with filter") {
    runStreamJob {
      _.
        filter(!_.startsWith("#")).
        flatMap(_.words).
        group.
        combineLength
    }
  }
  

  test("AllWithDslLowLevelAndCombiner") {
    runJob {
      MC[String, String, Int, String, Int](
        _.words.map { x => (x, 1) },
        (k, v) => (k, v.sum))
    }
  }

  test("AllWithDslLowLevel") {
    runJob {
      MR[String, String, Int, String, Int](
        _.words.map { x => (x, 1) },
        (k, v) => List((k, v.sum)))
    }
  }

}
