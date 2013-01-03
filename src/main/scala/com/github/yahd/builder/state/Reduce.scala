package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.Prelude._
import com.github.yahd.MR
import com.github.yahd.MonadicWithKeyLike
import com.github.yahd.FunctorWithTraversableValuesLike

class Reduce[A, B, C, D, E](m: MFunction[A, B, C], r: RFunction[B, C, D, E])
  extends MonadicWithKeyLike[D, E]
//  with FunctorWithTraversableValuesLike[E]
  with TerminalLike[A, B, C, D, E] {

//  override type OutFunctorOnAssociativeConmutative[E2] = Reduce[A, B, C, D, E2]
  override type OutFunctorWithKey[D2, E2] = Reduce[A, B, C, D2, E2]

  def mcr = MR(m, r)

  def flatMapGroup[D2, E2](f: (D, E) => Iterable[Grouping[D2, E2]]) =
    new Reduce[A, B, C, D2, E2](m, {
      (k, v) => r(k, v).flatMap { case (k2, v2) => f(k2, v2) }
    })
  //XXX
  //    new Reduce[A, B, C, D2, E2](m, Function.untupled(r.tupled >>> { _.flatMap(f.tupled) }))

//  override def genericCountValues[N: Numeric](f: A => Boolean) = map { (x: E) => fromInt(x.count(f)) }

}
