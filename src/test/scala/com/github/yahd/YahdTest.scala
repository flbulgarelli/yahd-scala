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
  
  //  def mcrFor[A, B, C, D, E](m: MFunction[A, B, C], c: Option[CFunction[B, C, B, C]], r: Option[RFunction[B, C, D, E]]) =
  //    (c, r) match {
  //      case (Some(_), Some(_)) => (m, c, r)
  //      case (Some(f), _) => (m, c, { (x, y) => (f(x, y)) })
  //      case (_, _) => (m, None, None)
  //    }
  
  
  
  test("dsl with group") {
    runStreamJob {
      _.
        concatMap(_.words).
        group.
        mapValuesLength
    }
  }
  test("dsl with groupOn") {
    runStreamJob {
      _.
        concatMap(_.words).
        groupOn(id).
        mapValuesLength
    }
  }
  test("dsl with combineLength") {
    runStreamJob {
      _.
        concatMap(_.words).
        group.
        combineLength
    }
  }

  test("dsl with groupMapping and combineSum") {
    runStreamJob {
      _.
        concatMap(_.words).
        groupMapping(const(1)).
        combineSum
    }
  }

  test("dsl with groupMappingOn and mapValuesReducing") {
    runStreamJob {
      _.
        concatMap(_.words).
        groupMappingOn(id)(const(1)).
        mapValuesReducing(_ + _)
    }
  }

  test("dsl with groupMappingOn and combine ") {
    runStreamJob {
      _.
        concatMap(_.words).
        groupMappingOn(id)(const(1)).
        combine(_ + _)
    }
  }
  test("dsl with mapValues -- not combinable") {
    runStreamJob {
      _.
        concatMap(_.words).
        group.
        mapValues(_.size)
    }
  }

  test("dsl with map") {
    runStreamJob {
      _.
        concatMap(_.words).
        map { x => (x, 1) }.
        groupMappingOn(_._1)(_._2).
        mapValues(_.sum)
    }
  }
  
  test("AllWithDslLowLevelAndCombiner") {
    runJob {
      new MCR(
        _.words.map { x => (x, 1) },
        Some(((k: String), (v: Iterable[Int])) => (k, v.sum)),
        Some(((k: String), (v: Iterable[Int])) => List((k, v.sum))))
    }
  }

  test("AllWithDslLowLevel") {
    runJob {
      new MCR(
        _.words.map { x => (x, 1) },
        None,
        Some(((k: String), (v: Iterable[Int])) => List((k, v.sum))))
    }
  }

}
