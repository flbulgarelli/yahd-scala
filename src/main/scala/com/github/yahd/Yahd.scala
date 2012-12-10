package com.github.yahd

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.WritableComparable
import org.apache.hadoop.io.BooleanWritable
import org.apache.hadoop.io.FloatWritable
import org.apache.hadoop.io.ByteWritable
import org.apache.hadoop.io.DoubleWritable

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
  type CFunction[A, B, C, D] = (A, Iterable[B]) => (C, D)
  type RFunction[A, B, C, D] = (A, Iterable[B]) => Iterable[(C, D)]

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

}
