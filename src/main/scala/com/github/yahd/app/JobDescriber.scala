package com.github.yahd.app

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job

import com.github.yahd._
import Yahd._
import builder._

trait JobDescriber {

  /**
   * Setups JarClass, Mapper, Combiner, Reducer, 
   * output key and value classes, and job name
   */
  def defineJob[B, WB, C, WC, D, WD, E, WE] //
  (jobName: String) //
  (jobDef: state.TerminalLike[String, B, C, D, E]) //
  (implicit aConverter: Converter[String, WString],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE], 
    dManifest: Manifest[WD], 
    eManifest: Manifest[WE]) = {
    
    val conf = new Configuration
    val job = new Job(conf)

    job.setJarByClass(getClass)
    
    val factory = jobDef.mcr.newMapReduceFactory
    
    app.AppGlobalConfig.init(factory.newMapper, factory.newCombiner.orNull, factory.newReducer.orNull)
    app.AppGlobalConfig.configureJob(job)
    
    job.setOutputKeyClass(manifest[WD].erasure)
    job.setOutputValueClass(manifest[WE].erasure)
  
    job.setJobName(jobName)
    
    job
  }
}