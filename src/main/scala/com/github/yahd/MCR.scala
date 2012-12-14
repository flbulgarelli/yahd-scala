package com.github.yahd

import Yahd._
import Prelude._
import scala.collection.JavaConversions._

import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer

case class MCR[A, B, C, D, E](m: MFunction[A, B, C],
  c: Option[CFunction[B, C, B, C]],
  r: Option[RFunction[B, C, D, E]]) {

  def newMapReduceFactory[WA, WB, WC, WD, WE](implicit
    aConverter: Converter[A, WA],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE]) = new Object {

    def newMapper = new Mapper[WLong, WA, WB, WC] {
      override def map(key: WLong, value: WA, context: Mapper[WLong, WA, WB, WC]#Context) {
        m(aConverter.unwrap(value)).foreach {
          case (k, v) =>
            context.write(bConverter.wrap(k), cConverter.wrap(v))
        }
      }
    }

    def newCombiner: Option[Reducer[WB, WC, WB, WC]] = None

    def newReducer = for (f <- r) yield new Reducer[WB, WC, WD, WE] {
      override def reduce(key: WB,
        values: JavaIterable[WC],
        context: Reducer[WB, WC, WD, WE]#Context) {
        f(bConverter.unwrap(key), values.map(cConverter.unwrap(_))).foreach {
          case (k, v) =>
            context.write(dConverter.wrap(k), eConverter.wrap(v))
        }
      }
    }
  }

}