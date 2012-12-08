package com.github.yahd

import java.lang.{ Iterable => JavaIterable }
import java.util.{ Iterator => JavaIterator }
import java.util.ArrayList
import java.util.{ List => JavaList }
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text

object Yahd extends Yahd
trait Yahd {
  type WLong = LongWritable
  type WInt = IntWritable
  type WString = Text


  implicit def text2String(text: Text) = text.toString
  implicit def string2Text(string: String) = new Text(string)
  implicit def int2IntWritable(i: Int) = new IntWritable(i)
  implicit def string2WordsOps(string: String) = new Object {
    def words = string.split(" ")
  }

  type MFunction[A, B, C] = A => Iterable[(B, C)]
  type CFunction[A, B, C, D] = (A, Iterable[B]) => (C, D)
  type RFunction[A, B, C, D] = (A, Iterable[B]) => Iterable[(C, D)]

  type JavaIterable[A] = java.lang.Iterable[A]

  def id[A] = { x: A => x }
  def const[A, B](x: B) = { _: A => x }

  trait Wrappable[A] {
    def wrap
  }

}