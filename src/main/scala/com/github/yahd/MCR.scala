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
    aType: Converter[A, WA],
    bType: Converter[B, WB],
    cType: Converter[C, WC],
    dType: Converter[D, WD],
    eType: Converter[E, WE]) = new Object {

    def newMapper = new Mapper[WLong, WA, WB, WC] {
      override def map(key: WLong, value: WA, context: Mapper[WLong, WA, WB, WC]#Context) {
        m(aType.unwrap(value)).foreach {
          case (k, v) =>
            context.write(bType.wrap(k), cType.wrap(v))
        }
      }
    }

    def newCombiner: Option[Reducer[WB, WC, WB, WC]] = None

    def newReducer = for (f <- r) yield new Reducer[WB, WC, WD, WE] {
      override def reduce(key: WB,
        values: JavaIterable[WC],
        context: Reducer[WB, WC, WD, WE]#Context) {
        f(bType.unwrap(key), values.map(cType.unwrap(_))).foreach {
          case (k, v) =>
            context.write(dType.wrap(k), eType.wrap(v))
        }
      }
    }
  }

}