package com.github.yahd
import org.apache.hadoop.io.WritableComparable

trait WType[A, B <: WritableComparable[_]] {
  def wrap: A => B
  def unwrap: B => A
}