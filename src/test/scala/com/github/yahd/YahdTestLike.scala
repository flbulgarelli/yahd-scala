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
  
  type YahdAssertion[D <: TestDriver[_, _, _, _]] =  D => Unit
  type YahdDriverAssertion = YahdAssertion[Driver]

  implicit def testDriver2RunnableDriver[D <: TestDriver[_, _, _, _]](driver: D) = new Object {
    def testThat(f: YahdAssertion[D]): Unit = {
      f(driver)
      driver.runTest
    }
  }
  
  def defineJob[A, WA, B, WB, C, WC,D, WD, E, WE] //
  (mcr: MCR[A, B, C, D, E]) //
  (implicit aConverter: Converter[A, WA],
    bConverter: Converter[B, WB], 
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE]) = 
    (mcr match {
      case M(m)          => newMapDriver[WLong, WA, WD, WE](m)
      case MC(m, c)      => newMapReduceDriver[WLong, WA, WD, WE, WD, WE](m, c, c)
      case FMCR(m, c, r) => newMapReduceDriver[WLong, WA, WB, WC, WD, WE](m, r, c)
      case MR(m, r)      => newMapReduceDriver[WLong, WA, WB, WC, WD, WE](m, r)
    }).asInstanceOf[Driver]

  def defineJob[B, WB, C, WC, D, WD, E, WE] //
  (mcrBuilder: MCRBuilder[String, B, C, D, E]) //
  (implicit aConverter: Converter[String, WString],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE]) =
    defineJob[String, WString, B, WB, C, WC, D, WD, E, WE] {
      buildMCR(mcrBuilder)
    }

}