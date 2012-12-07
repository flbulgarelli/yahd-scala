package com.github.yahd

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

import scala.collection.JavaConversions._

@RunWith(classOf[JUnitRunner])
class WordCountTest extends FunSuite with Yahd {

  var mapper = new WordCountMapper()
  var reducer = new WordCountReducer()
  var mapDriver = MapDriver.newMapDriver(mapper)
  var reduceDriver = ReduceDriver.newReduceDriver(reducer)
  var mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer)

  test("testMapper") {
    mapDriver.testThat { it =>
      it.withInput(new LongWritable(), "hello world hello hello")
      it.withOutput("hello", 1)
      it.withOutput("world", 1)
      it.withOutput("hello", 1)
      it.withOutput("hello", 1)
    }
  }

  test("testReducer") {
    reduceDriver.testThat { it =>
      it.withInput("world", List(
        new IntWritable(1),
        new IntWritable(1),
        new IntWritable(1)
      ))
      it.withOutput("world", 3)
    }
  }


  test("testAll") {
    mapReduceDriver.testThat { it =>
      it.withInput(new LongWritable(), "hello world hello hello")
      it.withOutput("hello", 3)
      it.withOutput("world", 1)
    }
  }
}
