package com.github.yahd.app.config
import com.github.yahd.MCR
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import com.github.yahd.Converter
import com.github.yahd._
import com.github.yahd.Yahd.m2Mapper
import com.github.yahd.Yahd.r2Reducer
import com.github.yahd.Yahd.c2Reducer
import com.github.yahd.app.AppGlobalConfig
import AppGlobalConfig.{init => initApp}

trait InputType extends JobConfiguration {

  def >>[A, WA, B, WB, C, WC, D, WD, E, WE] //
  (mcr: MCR[A, B, C, D, E]) //
  (implicit aConverter: Converter[A, WA],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE],
    bManifest: Manifest[WB],
    cManifest: Manifest[WC],
    dManifest: Manifest[WD],
    eManifest: Manifest[WE],
    job: Job) = {
    
    mcr match {
      case M(m)     => initApp[WA, WD, WE, WD, WE](m, null, null)
      case MC(m, c) => initApp[WA, WD, WE, WD, WE](m, c, c)
      case MR(m, r) => initApp[WA, WB, WC, WD, WE](m, null, r)
    }
    
    job.setMapOutputKeyClass(bManifest.erasure)
    job.setMapOutputValueClass(cManifest.erasure)
    job.setOutputKeyClass(dManifest.erasure)
    job.setOutputValueClass(eManifest.erasure)
    new AnyRef {
      def >>(out: OutputType) = ()
    }
  }
}

