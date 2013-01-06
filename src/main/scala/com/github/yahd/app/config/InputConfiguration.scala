package com.github.yahd.app.config
import com.github.yahd.MCR
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import com.github.yahd.Converter
import com.github.yahd._
import com.github.yahd.Yahd.m2Mapper
import com.github.yahd.Yahd.r2Reducer
import com.github.yahd.Yahd.c2Reducer
import com.github.yahd.app.JobFactory
import com.github.yahd.Yahd._

trait InputConfiguration[A] extends JobConfiguration {

  final def >>[WA, B, WB, C, WC, D, WD, E, WE](mcr: MCR[A, B, C, D, E]) //
  (implicit aConverter: Converter[A, WA],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE],
    bManifest: Manifest[WB],
    cManifest: Manifest[WC],
    dManifest: Manifest[WD],
    eManifest: Manifest[WE],
    jobFactory: JobFactory) = {
    jobFactory += this
    new ProcessConfiguration[A, WA, B, WB, C, WC, D, WD, E, WE](mcr)
  }

  final def >>[WA, B, WB, C, WC, D, WD, E, WE](mcrBuilder: MCRBuilder[A, B, C, D, E]) //
  (implicit aConverter: Converter[A, WA],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE],
    bManifest: Manifest[WB],
    cManifest: Manifest[WC],
    dManifest: Manifest[WD],
    eManifest: Manifest[WE],
    jobFactory: JobFactory): ProcessConfiguration[A, WA, B, WB, C, WC, D, WD, E, WE] =
    this >> mcrBuilder(from[A]).mcr
}

