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
import AppGlobalConfig.{ init => initApp }
import com.github.yahd.app.JobFactory
import com.github.yahd.Yahd._

trait InputConfiguration[A] extends JobConfiguration {

  def >>[WA, B, WB, C, WC, D, WD, E, WE](mcr: MCR[A, B, C, D, E]) //
  (implicit aConverter: Converter[A, WA],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE],
    bManifest: Manifest[WB],
    cManifest: Manifest[WC],
    dManifest: Manifest[WD],
    eManifest: Manifest[WE],
    jobFactory: JobFactory): ProcessType = {
    //XXX this should go into ProcessType
    mcr match {
      case M(m) => initApp[WA, WD, WE, WD, WE](m, null, null)
      case MC(m, c) => initApp[WA, WD, WE, WD, WE](m, c, c)
      case FMCR(m, c, r) => initApp[WA, WB, WC, WD, WE](m, c, r)
      case MR(m, r) => initApp[WA, WB, WC, WD, WE](m, null, r)
    }
    jobFactory += this
    new ProcessType(bManifest.erasure, cManifest.erasure, dManifest.erasure, eManifest.erasure)
  }

  def >>[WA, B, WB, C, WC, D, WD, E, WE](mcrBuilder: MCRBuilder[A, B, C, D, E]) //
  (implicit aConverter: Converter[A, WA],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE],
    bManifest: Manifest[WB],
    cManifest: Manifest[WC],
    dManifest: Manifest[WD],
    eManifest: Manifest[WE],
    jobFactory: JobFactory): ProcessType = this >> mcrBuilder(from[A]).mcr
}

