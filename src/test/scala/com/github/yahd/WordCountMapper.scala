package com.github.yahd
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.IntWritable

import com.github.yahd.Yahd._

class WordCountMapper extends Mapper[WLong, WString, WString, WInt] {

  override def map(key: WLong, value: WString, context: Mapper[WLong, WString, WString, WInt]#Context) =
    value.toString.words.foreach(context.write(_, 1)) 

}