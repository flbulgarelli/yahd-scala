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
    val reducer = factory.newReducer.get //FIXME

    var mapDriver = MapDriver.newMapDriver(mapper)

    var reduceDriver = ReduceDriver.newReduceDriver(reducer)
    var mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer)

    mapReduceDriver.testThat { it =>
      it.withInput(new WLong(), "hello world hello hello")
      it.withOutput("hello", 3)
      it.withOutput("world", 1)
    }
  }

  import builder.state

  def runStreamJob[B, WB, C, WC] //
  (mcrBuilder: (state.Initial[String] => state.TerminalLike[String, B, C, String, Int])) //
  (implicit aType: Converter[String, WString],
    bType: Converter[B, WB],
    cType: Converter[C, WC]) =
    {
      runJob[B, WB, C, WC] {
        val mcr = mcrBuilder(new state.Initial).mcr
        new MCR[String, B, C, String, Int](mcr._1, mcr._2, mcr._3)
      }
    }

}