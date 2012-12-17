package com.github.yahd.app
import com.github.yahd.MCR
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import com.github.yahd.Converter
import com.github.yahd._
import com.github.yahd.app.config.OutputKeyValueType

trait InputType extends JobConfiguration {

  def >>[A, WA, B, WB, C, WC, D, WD, E, WE] //
  (mcr: MCR[A, B, C, D, E]) //
  (implicit aConverter: Converter[A, WA],
    bConverter: Converter[B, WB],
    cConverter: Converter[C, WC],
    dConverter: Converter[D, WD],
    eConverter: Converter[E, WE],
    dManifest: Manifest[WD],
    eManifest: Manifest[WE]) = {
    val factory = mcr.newMapReduceFactory
    app.AppGlobalConfig.init(factory.newMapper, factory.newCombiner.orNull, factory.newReducer.orNull)
    new OutputKeyValueType(manifest[WD].erasure, manifest[WE].erasure)
  }
}

