package com.github.yahd

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.WritableComparable
import org.apache.hadoop.io.BooleanWritable
import org.apache.hadoop.io.FloatWritable
import org.apache.hadoop.io.ByteWritable
import org.apache.hadoop.io.DoubleWritable
import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.mapreduce.Mapper
import Prelude.Id
import scala.collection.JavaConversions._
import java.lang.{Iterable => JavaIterable}

object Yahd {
  /*Hadoop Writable Type Synonyms*/

  type WComparable = WritableComparable[_]

  type WLong = LongWritable
  type WInt = IntWritable
  type WBoolean = BooleanWritable
  type WByte = ByteWritable
  type WFloat = FloatWritable
  type WDouble = DoubleWritable

  type WString = Text

  /*Hadoop functions type synonyms */
  

  type MFunction[A, B, C] = A => Iterable[(B, C)]
  type RLikeFunction[A, B, C, D, Functor[_]] = (A, Iterable[B]) => Functor[(C, D)] 
  type CFunction[A, B] = RLikeFunction[A, B, A, B, Id]
  type RFunction[A, B, C, D] = RLikeFunction[A, B, C, D, Iterable]

  /*WritableComparable implicit converters*/

  abstract class WComparableConverter[A, WA <: WComparable { def get(): A }] extends Converter[A, WA] {
    def unwrap = _.get
  }

  implicit val intConverter = new WComparableConverter[Int, WInt] {
    def wrap = new WInt(_)
  }

  val longConverter = new WComparableConverter[Long, WLong] {
    def wrap = new WLong(_)
  }

  implicit val boolConverter = new WComparableConverter[Boolean, WBoolean] {
    def wrap = new WBoolean(_)
  }

  implicit val floatConverter = new WComparableConverter[Float, WFloat] {
    def wrap = new WFloat(_)
  }

  implicit val doubleConverter = new WComparableConverter[Double, WDouble] {
    def wrap = new WDouble(_)
  }

  implicit val stringConverter = new Converter[String, WString] {
    def wrap = new WString(_)
    def unwrap = _.toString
  }
  
  /*Common convertions**/

  implicit def text2String(text: Text) = text.toString
  implicit def string2Text(string: String) = new Text(string)
  implicit def int2IntWritable(i: Int) = new IntWritable(i)

  implicit def string2WordsOps(string: String) = new Object {
    def words = string.split(" ")
  }
  
  /* MCR builder */

  import builder.state

  type MCRBuilder[A, B, C, D, E] = state.Initial[A] => state.TerminalLike[A, B, C, D, E]

  def buildMCR[A, B, C, D, E](mcrBuilder: MCRBuilder[A, B, C, D, E]) =
    mcrBuilder(new state.Initial).mcr

  def from[A] = new state.Initial[A]

  /*Function to Hadoop Objects convertions*/

  private def newReducer0[B, C, D, E, WB, WC, WD, WE](r: RFunction[B, C, D, E])
    (implicit bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE]) = new Reducer[WB, WC, WD, WE] {
    override def reduce(key: WB,
      values: JavaIterable[WC],
      context: Reducer[WB, WC, WD, WE]#Context) {
      r(bConverter.unwrap(key), values.map(cConverter.unwrap(_))).foreach {
        case (k, v) =>
          context.write(dConverter.wrap(k), eConverter.wrap(v))
      }
    }
  }

  implicit def m2Mapper[A, B, C, WA, WB, WC](m: MFunction[A, B, C]) //
  (implicit aConverter: Converter[A, WA],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC]) = new Mapper[WLong, WA, WB, WC] {
    override def map(key: WLong, value: WA, context: Mapper[WLong, WA, WB, WC]#Context) {
      m(aConverter.unwrap(value)).foreach {
        case (k, v) =>
          context.write(bConverter.wrap(k), cConverter.wrap(v))
      }
    }
  }

  implicit def c2Reducer[B, C, WB, WC](c: CFunction[B, C]) //
  (implicit bConverter: Converter[B, WB],
    cConverter: Converter[C, WC]) = newReducer0[B, C, B, C, WB, WC, WB, WC]((x, y) => List(c(x, y)))

  implicit def r2Reducer[B, C, D, E, WB, WC, WD, WE](r: RFunction[B, C, D, E]) //
  (implicit bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE]) = newReducer0(r)

}
