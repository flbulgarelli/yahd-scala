package com.github.yahd.builder.state
import com.github.yahd.Yahd._
import com.github.yahd.Prelude._
import com.github.yahd._

import generic._

/**
 * Builder state reached after M, C and R functions have being registered
 * @author flbulgarelli
 */
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
  /**
   * Implicit conversion for enabling traversableValues operations when combineReduce state
   * has traversable as value
   */
  implicit def reduce2FunctorWithTraversableValues[A, B, C, D, E](combineReduce: CombineReduce[A, B, C, D, Traversable[E]]) =
    new AbstractReduceWithTraversableValuesLike[D, E] {
      override type OutFunctorWithKey[D2, E2] = CombineReduce[A, B, C, D2, E2]
      
      override def flatMapGroup[D2, E2](f: (D, Traversable[E]) => Traversable[Grouping[D2, E2]]) =
        combineReduce.flatMapGroup(f)
    }
}