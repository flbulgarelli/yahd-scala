package com.github.yahd

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.mrunit.TestDriver
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver
import org.junit.Before
import org.junit.Test
import org.apache.hadoop.io.IntWritable
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import java.lang.Boolean
import scala.collection.JavaConversions._
import scala.math.Numeric
import scala.Tuple2

@RunWith(classOf[JUnitRunner])
class YahdTest extends FunSuite with Yahd {

  type MFunction[A, B, C] = A => Iterable[(B, C)]
  type CFunction[A, B, C, D] = (A, Iterable[B]) => (C, D)
  type RFunction[A, B, C, D] = (A, Iterable[B]) => Iterable[(C, D)]

  case class MCR(
    m: MFunction[String, String, Int],
    c: Option[CFunction[String, Int, String, Int]],
    r: Option[RFunction[String, Int, String, Int]]) {
    def newMapper = new Mapper[WLong, Text, Text, WInt] {
      override def map(key: WLong, value: Text, context: Context) {
        m(value).foreach {
          case (k, v) =>
            context.write(k, v)
        }
      }
    }

    def newReducer = for (f <- r) yield new Reducer[Text, IntWritable, Text, IntWritable] {
      override def reduce(key: Text, values: JavaIterable[IntWritable], context: Context) {
        f(key, values.map { _.get }).foreach {
          case (k, v) =>
            context.write(k, v)
        }
      }
    }
  }

//  def mcrFor[A, B, C, D, E](m: MFunction[A, B, C], c: Option[CFunction[B, C, B, C]], r: Option[RFunction[B, C, D, E]]) =
//    (c, r) match {
//      case (Some(_), Some(_)) => (m, c, r)
//      case (Some(f), _) => (m, c, { (x, y) => (f(x, y)) })
//      case (_, _) => (m, None, None)
//    }

  trait MCRBuilderTerminalState {
    def mcr : AnyRef
  }
  
  trait MCRBuilderState[+A] {
    def map[B](f: A => B) =
      concatMap { x => List(f(x)) }

    def filter(f: A => Boolean) =
      concatMap { x => if (f(x)) List(x) else Nil }

    def concatMap[B](f: A => Iterable[B]): MCRBuilderState[B]
  }

  class MCRBuilderInitialState[+A] extends MCRBuilderState[A] {
    override def map[B](f: A => B) =
      super.map(f).asInstanceOf[MCRBuilderMapperState[A, B]]

    override def filter(f: A => Boolean) =
      super.filter(f).asInstanceOf[MCRBuilderMapperState[A, A]]

    def concatMap[B](f: A => Iterable[B]) =
      new MCRBuilderMapperState[A, B](f)
  }

  object MCRBuilderInitialState extends MCRBuilderInitialState[Nothing]

  class MCRBuilderMapperState[A, B](pm: A => Iterable[B]) extends MCRBuilderState[B] {

    override def map[C](f: B => C) =
      super.map(f).asInstanceOf[MCRBuilderMapperState[A, C]]

    override def filter(f: B => Boolean) =
      super.filter(f).asInstanceOf[MCRBuilderMapperState[A, B]]

    def concatMap[C](f: B => Iterable[C]) {
      new MCRBuilderMapperState[A, C](
        pm.andThen(_.flatMap(f)))
    }
    def group = groupMappingOn(id)(id)

    def groupMapping[C](mappingFunction: B => C) =
      groupMappingOn(id)(mappingFunction)

    def groupOn[C](grouppingFunction: B => C) =
      groupMappingOn(grouppingFunction)(id)

    def groupMappingOn[C, D](g: B => C)(m: B => D) =
      new MCRBuilderGrouperState[A, C, D](pm.andThen(_.map { x => (g(x), m(x)) }))
  }

  implicit def grouperMCRStream2numericGrouperMCRStream[A, B, C](stream: MCRBuilderGrouperState[A, B, C])(implicit n: Numeric[C]) =
    new Object {
      def mapValuesSum = stream.combine(n.plus(_, _))
      def combineSum = mapValuesSum
    }

  //TODO implement combiners
  class MCRBuilderGrouperState[A, B, C](m: MFunction[A, B, C]) 
  	extends MCRBuilderState[(B, Iterable[C])] 
    with MCRBuilderTerminalState {

    def mapValues[D](f: Iterable[C] => D) = map { (k, vs) => (k, f(vs)) }
    def mapKeys[D](f: B => D) = map { (k, vs) => (f(k), vs) }
    def map[D, E](f: (B, Iterable[C]) => (D, E)) =
      new MCRBuilderReducerState[A, B, C, D, E](m, { (k, vs) => Iterable(f(k, vs)) })

    def mapValuesFolding[D](initial: D)(f: (D, C) => D) =
      mapValues(_.foldLeft(initial)(f))

    def combineWith[D] = mapValuesFolding[D] _

    def mapValuesReducing(f: (C, C) => C) = mapValues(_.reduce(f))

    def combine = mapValuesReducing _

    def mapValuesLength = mapValuesFolding(0) { (x, y) => x + 1 }

    def combineLength = mapValuesLength

    def mcr = (m, None, None)
  }

  class MCRBuilderReducerState[A, B, C, D, E](m: MFunction[A, B, C], r: RFunction[B, C, D, E])
    extends MCRBuilderTerminalState {
    def mcr = (m, None, r)
  }

  def runStreamJob(mcrBuilder: (MCRBuilderInitialState[String] => MCRBuilderTerminalState)) = {
    runJob {
      mcrBuilder(MCRBuilderInitialState).mcr
    }
  }

  def runJob(mcr: => MCR) {
    val mapper = mcr.newMapper
    val reducer = mcr.newReducer

    var mapDriver = MapDriver.newMapDriver(mapper)

    var reduceDriver = ReduceDriver.newReduceDriver(reducer)
    var mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer)

    mapReduceDriver.testThat { it =>
      it.withInput(new LongWritable(), "hello world hello hello")
      it.withOutput("hello", 3)
      it.withOutput("world", 1)
    }
  }

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
        groupMapping(id)(const(1)).
        mapValuesReducing(_ + _)
    }
  }

  test("dsl with groupMappingOn and combine ") {
    runStreamJob {
      _.
        concatMap(_.words).
        groupMapping(id)(const(1)).
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
      MCR({
        _.words.map { x => (x, 1) }
      }, {
        (k, v) => List((k, v.sum()))
      }, {
        (k, v) => List((k, v.sum()))
      })
    }
  }

  test("AllWithDslLowLevel") {
    runJob {
      MCR({
        _.words.map { x => (x, 1) }
      }, None, {
        (k, v) => List((k, v.sum()))
      })
    }
  }

}
