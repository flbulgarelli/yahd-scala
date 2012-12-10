package com.github.yahd
import org.apache.hadoop.mrunit.TestDriver
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver
import com.github.yahd.Yahd._
import org.apache.hadoop.io.WritableComparable

trait YahdTestLike {

  implicit def testDriver2RunnableDriver[D <: TestDriver[_, _, _, _]](driver: D) = new Object {
    def testThat(f: Function[D, Unit]): Unit = {
      f(driver)
      driver.runTest
    }
  }
  
  def runJob[B, WB, C, WC] //
  (mcr: => MCR[String, B, C, String, Int]) //
  (implicit aType: Converter[String, WString],
    bType: Converter[B, WB],
    cType: Converter[C, WC]) {
    val factory = mcr.newMapReduceFactory
    
    val mapper = factory.newMapper
    var mapDriver = MapDriver.newMapDriver(mapper)
    
    val mapReduceDriver = (factory.newCombiner, factory.newReducer) match {
      case (Some(combiner), Some(reducer)) => MapReduceDriver.newMapReduceDriver(mapper, reducer, combiner)
      case (None,           Some(reducer)) => MapReduceDriver.newMapReduceDriver(mapper, reducer)
      case (None,           None)          => throw new Exception("unsupported yet")
      case (_,              _)             => throw new Exception("unespecified yet")
    }

    mapReduceDriver.testThat { it =>
      it.withInput(new WLong(), "hello world hello hello")
      it.withOutput("hello", 3)
      it.withOutput("world", 1)
    }
  }

  def runStreamJob[B, WB, C, WC] //
  (mcrBuilder: MCRBuilder[String, B, C, String, Int]) //
  (implicit aType: Converter[String, WString],
    bType: Converter[B, WB],
    cType: Converter[C, WC]) = {
    runJob[B, WB, C, WC] {
      buildMCR(mcrBuilder)
    }
  }

}