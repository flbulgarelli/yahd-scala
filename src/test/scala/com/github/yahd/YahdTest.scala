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

  type AnyMR = MR[_, _, _, _, _]
  type AnyMC = MC[_, _, _, _, _]
  type AnyM = M[_, _, _, _, _]
  type AnyFMCR = FMCR[_, _, _, _, _]

  test(" M ") {
    from[String].map(x => (x, 1)).mcr: AnyM
    from[String].filter { x => x.length() > 10 }.map(x => (x, 1)).mcr: AnyM
    from[String].flatMap(x => List((x, 1))).mcr: AnyM
  }

  test(" MR ") {
    from[String].map(id).group.map(_.size).mcr: AnyMR
    from[String].map(id).groupMapping(const(1)).reduceValues(_ + _).mcr: AnyMR
    from[String].map(id).group.mapValues(const(1)).sumValues.mcr: AnyMR
    from[String].map(id).group.mapValues(const(1)).sumValues.map { x => x.doubleValue() / 100 }.mcr: AnyMR
    from[String].map(id).group.takeValues(10).mapValues(_.size).sumValues.mcr: AnyMR
    from[String].map(id).groupMappingOn(_.head)(_.size).map { x => x.sum / x.size }.mcr: AnyMR
  }

  test(" MC ") {
    from[String].map(id).groupMapping(const(1)).minValues.mcr: AnyMC
    from[String].map(id).groupMapping(const(1)).sumValues.mcr: AnyMC
    from[String].map(id).groupMapping(const(1)).reduceValuesUsingCombiner(_ + _).mcr: AnyMC
    from[String].map(id).group.mapValuesUsingMapper(const(1)).sumValues.mcr: AnyMC
  }

  test(" FMCR ") {
    from[String].map(id).group.mapValuesUsingMapper(const(1)).sumValues.map { x => x.doubleValue() / 100 }.mcr: AnyFMCR
    from[String].map(id).groupMappingOn(_.charAt(0))(const(1)).reduceValuesUsingCombiner(_ + _).map { x => x.doubleValue() / 100 }.mcr: AnyFMCR
  }

  test("dsl with group") {
    runStreamJob {
      _.
        flatMap(_.words).
        group.
        lengthValues
    }
  }

  test("dsl with mapWithKey") {
    runStreamJob { stream =>
      stream
        .flatMap(_.words)
        .group
        .mapWithKey((x, y) => y.size)
    }
  }

  test("dsl with mapValuesUsingMapper") {
    runStreamJob { stream =>
      stream
        .flatMap(_.words)
        .group
        .mapValuesUsingMapper(const(1))
        .sumValues
    }
  }

  test("dsl with mapValues") {
    runStreamJob { stream =>
      stream
        .flatMap(_.words)
        .group
        .mapValues(const(1))
        .sumValues
    }
  }

  test("dsl with groupOn") {
    runStreamJob {
      _.
        flatMap(_.words).
        groupOn(id).
        lengthValues
    }
  }
  test("dsl with lengthValues") {
    runStreamJob {
      _.
        flatMap(_.words).
        group.
        lengthValues
    }
  }

  test("dsl with groupMapping and sumValues") {
    runStreamJob {
      _.
        flatMap(_.words).
        groupMapping(const(1)).
        sumValues
    }
  }

  test("dsl with groupMappingOn and mapValuesReducing") {
    runStreamJob {
      _.
        flatMap(_.words).
        groupMappingOn(id)(const(1)).
        reduceValues(_ + _)
    }
  }

  test("dsl with groupMappingOn and combine ") {
    runStreamJob {
      _.
        flatMap(_.words).
        groupMappingOn(id)(const(1)).
        reduceValuesUsingCombiner(_ + _)
    }
  }
  import Prelude.Grouping.onValue
  test("dsl with mapValues -- not combinable") {
    runStreamJob {
      _.
        flatMap(_.words).
        group.
        mapGroup(onValue(_.size))
    }
  }

  test("dsl with map") {
    runStreamJob {
      _.
        flatMap(_.words).
        map { x => (x, 1) }.
        groupMappingOn(_._1)(_._2).
        mapGroup((k, vs) => (k, vs.sum))
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
        lengthValues
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
