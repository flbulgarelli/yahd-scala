package com.github.yahd

import Yahd._
import Prelude._
import scala.collection.JavaConversions._

import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer

//FIXME support writable types

class MCR[C, WC <: WComparable](m: MFunction[String, String, C],
  c: Option[CFunction[String, C, String, C]],
  r: Option[RFunction[String, C, String, Int]])(implicit cc: WType[C, WC]) {

  def newMapper = new Mapper[WLong, WString, WString, WC] {
    override def map(key: WLong, value: WString, context: Mapper[WLong, WString, WString, WC]#Context) {
      m(value.toString).foreach {
        case (k, v) =>
          context.write(k, cc.wrap(v))
      }
    }
  }

  def newReducer = for (f <- r) yield new Reducer[WString, WC, WString, WInt] {
    override def reduce(key: WString,
      values: JavaIterable[WC],
      context: Reducer[WString, WC, WString, WInt]#Context) {
      f(key, values.map(cc.unwrap(_))).foreach {
        case (k, v) =>
          context.write(k, v)
      }

    }
  }

}