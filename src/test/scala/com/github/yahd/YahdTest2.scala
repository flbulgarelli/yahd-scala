package com.github.yahd
import org.apache.hadoop.mrunit.TestDriver
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver
import com.github.yahd.Yahd._

object YahdTest2 {
  implicit def testDriver2RunnableDriver[D <: TestDriver[_, _, _, _]](driver: D) = new Object {
    def testThat(f: Function[D, Unit]): Unit = {
      f(driver)
      driver.runTest
    }
  }

  def runJob(mcr: => MCR) {
    val mapper = mcr.newMapper
    val reducer = mcr.newReducer.get //FIXME

    var mapDriver = MapDriver.newMapDriver(mapper)

    var reduceDriver = ReduceDriver.newReduceDriver(reducer)
    var mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer)

    mapReduceDriver.testThat { it =>
      it.withInput(new WLong(), "hello world hello hello")
      it.withOutput("hello", 3)
      it.withOutput("world", 1)
    }
  }
}