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

  abstract class SimpleWType[A, WA <: WComparable { def get(): A }] extends WType[A, WA] {
    def unwrap = _.get
  }

  implicit val intWType = new SimpleWType[Int, WInt] {
    def wrap = new WInt(_)
  }

  val longWType = new SimpleWType[Long, WLong] {
    def wrap = new WLong(_)
  }

  implicit val boolWType = new SimpleWType[Boolean, WBoolean] {
    def wrap = new WBoolean(_)
  }

  implicit val floatWType = new SimpleWType[Float, WFloat] {
    def wrap = new WFloat(_)
  }

  implicit val doubleWType = new SimpleWType[Double, WDouble] {
    def wrap = new WDouble(_)
  }

  implicit val stringWType = new WType[String, WString] {
    def wrap = new WString(_)
    def unwrap = _.toString
  }

  implicit def text2String(text: Text) = text.toString
  implicit def string2Text(string: String) = new Text(string)
  implicit def int2IntWritable(i: Int) = new IntWritable(i)

  implicit def string2WordsOps(string: String) = new Object {
    def words = string.split(" ")
  }

}
