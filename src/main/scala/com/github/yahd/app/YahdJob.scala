package com.github.yahd.app
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job

import com.github.yahd._
import Yahd._
import builder._

trait YahdJob {
  val conf = new Configuration
  val job = new Job(conf)

  job.setJarByClass(getClass)

  def defineJob[B, WB, C, WC, D, WD, E, WE] //
  (jobDef: state.TerminalLike[String, B, C, D, E]) //
  (implicit aType: Converter[String, WString],
    bType: Converter[B, WB],
    cType: Converter[C, WC],
    dType: Converter[D, WD],
    eType: Converter[E, WE]) = {
    val factory = jobDef.mcr.newMapReduceFactory
    app.App.init(factory.newMapper, factory.newCombiner.orNull, factory.newReducer.orNull)
    app.App.configureJob(job)
  }
}