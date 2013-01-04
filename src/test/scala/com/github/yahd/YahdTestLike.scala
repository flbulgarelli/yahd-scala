package com.github.yahd
import org.apache.hadoop.mrunit.TestDriver
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver
import com.github.yahd.Yahd._
import org.apache.hadoop.io.WritableComparable
import MapReduceDriver._
import MapDriver._
trait YahdTestLike {

  type Driver = TestDriver[_, _, _, _] {
    def withInput(x: Any, y: Any): Any
    def withOutput(x: Any, y: Any): Any
  }

  implicit def testDriver2RunnableDriver[D <: TestDriver[_, _, _, _]](driver: D) = new Object {
    def testThat(f: Function[D, Unit]): Unit = {
      f(driver)
      driver.runTest
    }
  }
  
  def runJob[B, WB, C, WC] //
  (mcr: => MCR[String, B, C, String, Int]) //
  (implicit aConverter: Converter[String, WString],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC]) {
    
    
    val mapReduceDriver = mcr match {
      case M(m)       => newMapDriver[WLong, WString, WString, WInt](m)
      case MC(m, c)   => newMapReduceDriver[WLong, WString, WString, WInt, WString, WInt](m, c, c)
      case FMCR(m, c, r) => newMapReduceDriver[WLong, WString, WB, WC, WString, WInt](m, r, c)
      case MR(m, r)   => newMapReduceDriver[WLong, WString, WB, WC, WString, WInt](m, r)
    }

    mapReduceDriver.asInstanceOf[Driver].testThat { it =>
      it.withInput(new WLong(), "hello world hello hello" : WString)
      it.withOutput("hello" : WString, 3 : WInt)
      it.withOutput("world" : WString, 1 : WInt)
    }
  }

  def runStreamJob[B, WB, C, WC] //
  (mcrBuilder: MCRBuilder[String, B, C, String, Int]) //
  (implicit aConverter: Converter[String, WString],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC]) = {
    runJob[B, WB, C, WC] {
      buildMCR(mcrBuilder)
    }
  }

}