package com.github.yahd
import org.apache.hadoop.mapreduce.Mapper

import com.github.yahd.Yahd.int2IntWritable
import com.github.yahd.Yahd.string2Text
import com.github.yahd.Yahd.string2WordsOps

import Yahd.WInt
import Yahd.WLong
import Yahd.WString

class WordCountMapper extends Mapper[WLong, WString, WString, WInt] {

  override def map(key: WLong, value: WString, context: Mapper[WLong, WString, WString, WInt]#Context) =
    value.toString.words.foreach(context.write(_, 1)) 

}