package com.github.yahd
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import Yahd._
import Prelude._
import Grouping._

@RunWith(classOf[JUnitRunner])
class MCRBuilderStructureTest extends FunSuite {

  type AnyMR = MR[_, _, _, _, _]
  type AnyMC = MC[_, _, _, _, _]
  type AnyM = M[_, _, _, _, _]
  type AnyFMCR = FMCR[_, _, _, _, _]

  test(" M ") {
    from[String].map(x => (x, 1)).mcr: AnyM
    from[String].filter { x => x.length() > 10 }.map(unitary(_)).mcr: AnyM
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

}