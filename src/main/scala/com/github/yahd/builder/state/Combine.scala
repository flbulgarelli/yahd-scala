package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.Prelude.Grouping
import com.github.yahd._

import generic._

/**
 * Builder state reached after and M and  C functions have being registered
 * 
 * @author flbulgarelli
 **/
class Combine[A, B, C](m: MFunction[A, B, C], c: CFunction[B, C])
  extends MonadicWithKeyLike[B, C]
  with TerminalLike[A, B, C, B, C] {

  override type OutFunctorWithKey[D, E] = CombineReduce[A, B, C, D, E]

  override def mcr = MC(m, c)

  override def flatMapGroup[D, E](f: (B, C) => Traversable[Grouping[D, E]]) =
    r {
      (k, vs) => c(k, vs) match { case (k2, v2) => f(k2, v2) }
    }

  def r[D, E](r: RFunction[B, C, D, E]) = new CombineReduce[A, B, C, D, E](m, c, r)

}
