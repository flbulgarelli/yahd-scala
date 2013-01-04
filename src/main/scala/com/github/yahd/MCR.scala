package com.github.yahd

import Yahd._
import Prelude._

import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer

sealed abstract class MCR[A, B, C, D, E]

case class M[A, B, C, D, E](m: MFunction[A, D, E]) extends MCR[A, B, C, D, E]
case class MC[A, B, C, D, E](m: MFunction[A, D, E], c: CFunction[D, E]) extends MCR[A, B, C, D, E]
case class MR[A, B, C, D, E](m: MFunction[A, B, C], r: RFunction[B, C, D, E]) extends MCR[A, B, C, D, E]
case class FMCR[A,B,C,D,E](m: MFunction[A, B, C], c: CFunction[B, C], r: RFunction[B, C, D, E]) extends MCR[A, B, C, D, E]
