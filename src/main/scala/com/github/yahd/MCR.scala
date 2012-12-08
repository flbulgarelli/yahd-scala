package com.github.yahd

import scala.annotation.implicitNotFound
import scala.collection.JavaConversions.iterableAsScalaIterable
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer
import com.github.yahd.Yahd.CFunction
import com.github.yahd.Yahd.MFunction
import com.github.yahd.Yahd.RFunction
import com.github.yahd.Yahd.JavaIterable
import com.github.yahd.Yahd.WInt
import com.github.yahd.Yahd.WLong
import com.github.yahd.Yahd.WString
import com.github.yahd.Yahd.int2IntWritable
import com.github.yahd.Yahd.string2Text
import com.github.yahd.Yahd.text2String
import org.apache.hadoop.io.WritableComparable

//FIXME support writable types

class MCR[C, WC <: WritableComparable[_]](m: MFunction[String, String, C],
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