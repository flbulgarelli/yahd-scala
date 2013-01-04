package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.Prelude.Grouping
import com.github.yahd._

import generic._

class Combine[A, B, C](m: MFunction[A, B, C], c: CFunction[B, C])
  extends MonadicWithKeyLike[B, C]
  with TerminalLike[A, B, C, B, C] {

  override type OutFunctorWithKey[D, E] = CombineReduce[A, B, C, D, E]

  override def mcr = MC(m, c)

  override def flatMapGroup[D, E](f: (B, C) => Iterable[Grouping[D, E]]) =
    new CombineReduce[A, B, C, D, E](m, c, {
      (k, vs) => c(k, vs) match { case (k2, v2) => f(k2, v2) }
    })

}
