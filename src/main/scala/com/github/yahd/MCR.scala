package com.github.yahd

import Yahd._
import Prelude._

import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer

/**
 * A, maybe incomplete, sequence of M-C-R Yahd computations:
 *
 * * M : Known in Hadoop as "Map". Is the computation that takes place before grouping. It is of type [[MFunction]]
 * * C : Known in Hadoop as "Combine". Is an optional computation that takes place after M and before R, if any. It is of type [[CFunction]]
 * * R : Known in Hadoop as "Reduce". Is an optional computation that takes place after C, if any. It is of type [[RFunction]]
 *
 * @author flbulgarelli
 */
sealed abstract class MCR[A, B, C, D, E]

/**
 * A sequence that just contains an M computation
 */
case class M[A, B, C, D, E](m: MFunction[A, D, E]) extends MCR[A, B, C, D, E]
/**
 * A sequence that contains M and C computations
 */
case class MC[A, B, C, D, E](m: MFunction[A, D, E], c: CFunction[D, E]) extends MCR[A, B, C, D, E]
/**
 * A sequence that contains M and R computations
 */
case class MR[A, B, C, D, E](m: MFunction[A, B, C], r: RFunction[B, C, D, E]) extends MCR[A, B, C, D, E]
/**
 * A (full) sequence that contains M, C  and R computations
 */
case class FMCR[A, B, C, D, E](m: MFunction[A, B, C], c: CFunction[B, C], r: RFunction[B, C, D, E]) extends MCR[A, B, C, D, E]
