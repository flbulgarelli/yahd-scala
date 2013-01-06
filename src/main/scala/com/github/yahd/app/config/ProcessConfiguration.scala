package com.github.yahd.app.config
import org.apache.hadoop.mapreduce.Job
import com.github.yahd._
import com.github.yahd.app.registry.MappersRegistry.registerMapper;
import com.github.yahd.app.registry.ReducersRegistry.registerReducer;

import Yahd._
import app.pool._
import app.registry._
import app.JobFactory
import MappersRegistry.registerMapper
import ReducersRegistry.registerReducer

class ProcessConfiguration[A, WA, B, WB, C, WC, D, WD, E, WE](mcr: MCR[A, B, C, D, E]) //
  (implicit aConverter: Converter[A, WA],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE],
    bManifest: Manifest[WB],
    cManifest: Manifest[WC],
    dManifest: Manifest[WD],
    eManifest: Manifest[WE],
    jobFactory: JobFactory) extends JobConfiguration {

  override def apply(cmdLine: Array[String], job: Job) {
    mcr match {
      case M(m) => { 
        val mapperClass = registerMapper[WA, WD, WE](m)
        job.setMapperClass(mapperClass)
      }
      case MC(m, c) => {
        val mapperClass = registerMapper[WA, WD, WE](m)
        val reducerClass = registerReducer[WD, WE, WD, WE](c)
        job.setMapperClass(mapperClass)
        job.setCombinerClass(reducerClass)
        job.setReducerClass(reducerClass)
      }
      case FMCR(m, c, r) => {
        val mapperClass = registerMapper[WA, WB, WC](m)
        val combinerClass = registerReducer[WB, WC, WB, WC](c)
        val reducerClass = registerReducer[WB, WC, WD, WE](r)
        job.setMapperClass(mapperClass)
        job.setCombinerClass(combinerClass)
        job.setReducerClass(reducerClass)
      }
      case MR(m, r) => {
        val mapperClass = registerMapper[WA, WB, WC](m)
        val reducerClass = registerReducer[WB, WC, WD, WE](r)
        job.setMapperClass(mapperClass)
        job.setReducerClass(reducerClass)
      }
    }
    job.setMapOutputKeyClass(bManifest.erasure)
    job.setMapOutputValueClass(cManifest.erasure)
    job.setOutputKeyClass(dManifest.erasure)
    job.setOutputValueClass(eManifest.erasure)
  }

  final def >>(out: OutputConfiguration)(implicit jobFactory: JobFactory) {
    jobFactory += this
    jobFactory += out
  }

}