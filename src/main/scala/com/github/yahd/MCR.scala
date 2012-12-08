package com.github.yahd

import com.github.yahd.Yahd._
import scala.collection.JavaConversions._
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.io.Text


case class MCR( m: MFunction[String, String, Int],
  c: Option[CFunction[String, Int, String, Int]],
  r: Option[RFunction[String, Int, String, Int]]) {
  
  def newMapper = new Mapper[WLong, Text, Text, WInt] {
    override def map(key: WLong, value: Text, context: Mapper[WLong, Text, Text, WInt]#Context) {
      m(value).foreach {
        case (k, v) =>
          context.write(k, v)
      }
    }
  }

  def newReducer = for (f <- r) yield new Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, 
        values: JavaIterable[IntWritable], 
        context: Reducer[Text, IntWritable, Text, IntWritable]#Context) {
      f(key, values.map(_.get)).foreach {
        case (k, v) =>
          context.write(k, v)
      }
    }
  }

}