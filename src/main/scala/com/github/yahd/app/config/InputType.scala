package com.github.yahd.app
import com.github.yahd.MCR
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import com.github.yahd.Converter
import com.github.yahd._

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
    val factory = mcr.newMapReduceFactory
    app.AppGlobalConfig.init(factory.newMapper, factory.newCombiner.orNull, factory.newReducer.orNull)
    job.setMapOutputKeyClass(bManifest.erasure)
    job.setMapOutputValueClass(cManifest.erasure)
    job.setOutputKeyClass(dManifest.erasure)
    job.setOutputValueClass(eManifest.erasure)
    new AnyRef {
      def >>(out: OutputType) = ()
    }
  }
}

