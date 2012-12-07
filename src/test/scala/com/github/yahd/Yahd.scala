package com.github.yahd

import java.lang.{Iterable => JavaIterable}
import java.util.{Iterator => JavaIterator}
import java.util.ArrayList
import java.util.{List => JavaList}

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mrunit.TestDriver

trait Yahd {
  type WLong = LongWritable
  type WInt = IntWritable

  implicit def testDriver2RunnableDriver[D <: TestDriver[_, _, _, _]](driver: D) = new Object {
    def testThat(f: Function1[D, Unit]): Unit = {
      f(driver)
      driver.runTest
    }
  }

  implicit def text2String(text: Text) = text.toString
  implicit def string2Text(string: String) = new Text(string)
  implicit def int2IntWritable(i: Int) = new IntWritable(i)
  implicit def string2WordsOps(string:String) = new Object {
    def words = string.split(" ")
  }

  type JavaIterable[A] = java.lang.Iterable[A]  
  
  def id[A] = { x:A => x}
  def const[A, B](x:B) = { _:A => x}
  
  trait Wrappable[A] {
    def wrap
  }
  

}