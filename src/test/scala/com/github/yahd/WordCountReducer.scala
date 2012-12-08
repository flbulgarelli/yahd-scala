package com.github.yahd
import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text

import com.github.yahd.Yahd._
import scala.collection.JavaConversions._
class WordCountReducer extends Reducer[WString, WInt, WString, WInt] {

  override def reduce(key: WString, values: JavaIterable[WInt], context: Reducer[WString, WInt, WString, WInt]#Context) =
    context.write(key, values.map(_.get).sum)
}