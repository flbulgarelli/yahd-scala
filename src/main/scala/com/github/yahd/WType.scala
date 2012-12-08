package com.github.yahd
import org.apache.hadoop.io.WritableComparable
import Yahd._

trait WType[A, WA <: WComparable] {
  def wrap: A => WA
  def unwrap: WA => A
}