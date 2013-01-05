package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.Prelude._
import com.github.yahd.MR

import generic._

class Reduce[A, B, C, D, E](
  private val m: MFunction[A, B, C],
  private val r: RFunction[B, C, D, E])
  extends AbstractReduce[A, B, C, D, E](m, r) {

  override type OutFunctorWithKey[D2, E2] = Reduce[A, B, C, D2, E2]

  override def mcr = MR(m, r)

  protected override def newReduce[D2, E2](r: RFunction[B, C, D2, E2]) =
    new Reduce[A, B, C, D2, E2](m, r)
}

object Reduce {
  /**
   * Implicit conversion for enabling traversableValues operations when reduce state
   * has traversable as value
   */
  implicit def reduce2FunctorWithTraversableValues[A, B, C, D, E](reduce: Reduce[A, B, C, D, Iterable[E]]) =
    new AbstractReduceWithTraversableValuesLike[E] {
      override type OutFunctorOnAssociativeConmutative[E2] = Reduce[A, B, C, D, E2]
      override def map[E2](f: Iterable[E] => E2) = reduce.map(f)
    }
}