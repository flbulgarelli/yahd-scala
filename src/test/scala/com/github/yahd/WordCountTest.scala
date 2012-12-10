package com.github.yahd

import scala.collection.JavaConversions.seqAsJavaList
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import com.github.yahd.YahdTest2.testDriver2RunnableDriver
import com.github.yahd.Yahd.int2IntWritable
import com.github.yahd.Yahd.string2Text
import Yahd.WLong
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WordCountTest extends FunSuite {

  var mapper = new WordCountMapper()
  var reducer = new WordCountReducer()
  var mapDriver = MapDriver.newMapDriver(mapper)
  var reduceDriver = ReduceDriver.newReduceDriver(reducer)
  var mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer)

  test("testMapper") {
    mapDriver.testThat { it =>
      it.withInput(new WLong(), "hello world hello hello")
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
      it.withInput(new WLong(), "hello world hello hello")
      it.withOutput("hello", 3)
      it.withOutput("world", 1)
    }
  }
}
