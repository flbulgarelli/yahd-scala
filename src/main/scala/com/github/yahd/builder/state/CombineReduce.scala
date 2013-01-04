package com.github.yahd.builder.state
import com.github.yahd.Yahd._
import com.github.yahd.Prelude._
import com.github.yahd._

import generic._

class CombineReduce[A, B, C, D, E](
  private val m: MFunction[A, B, C],
  private val c: CFunction[B, C],
  private val r: RFunction[B, C, D, E])
  extends AbstractReduce[A, B, C, D, E](m, r) {

  override type OutFunctorWithKey[D2, E2] = CombineReduce[A, B, C, D2, E2]

  override def mcr = FMCR(m, c, r)

  protected override def newReduce[D2, E2](r: RFunction[B, C, D2, E2]) =
    new CombineReduce[A, B, C, D2, E2](m, c, r)

}

object CombineReduce {
  implicit def reduce2FunctorWithTraversableValues[A, B, C, D, E](combineReduce: CombineReduce[A, B, C, D, Iterable[E]]) =
    new AbstractReduceWithTraversableValuesLike[E] {
      override type OutFunctorOnAssociativeConmutative[E2] = CombineReduce[A, B, C, D, E2]
      override def map[E2](f: Iterable[E] => E2) = combineReduce.map(f)
    }
}