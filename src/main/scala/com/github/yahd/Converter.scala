package com.github.yahd
import org.apache.hadoop.io.WritableComparable
import Yahd._

trait Converter[A, WA] {
  def wrap: A => WA
  def unwrap: WA => A
}