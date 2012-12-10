package com.github.yahd

import scala.collection.JavaConversions.iterableAsScalaIterable

import org.apache.hadoop.mapreduce.Reducer

import com.github.yahd.Prelude.JavaIterable
import com.github.yahd.Yahd.int2IntWritable

import Prelude.JavaIterable
import Yahd.WInt
import Yahd.WString

class WordCountReducer extends Reducer[WString, WInt, WString, WInt] {

  override def reduce(key: WString, values: JavaIterable[WInt], context: Reducer[WString, WInt, WString, WInt]#Context) =
    context.write(key, values.map(_.get).sum)
}