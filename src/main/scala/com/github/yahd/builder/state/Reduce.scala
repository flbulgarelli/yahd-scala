package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.Prelude._
import com.github.yahd.MR

import generic._

class Reduce[A, B, C, D, E](private val m: MFunction[A, B, C], private val r: RFunction[B, C, D, E])
  extends MonadicWithKeyLike[D, E]
  with TerminalLike[A, B, C, D, E] {

  override type OutFunctorWithKey[D2, E2] = Reduce[A, B, C, D2, E2]

  def mcr = MR(m, r)

  def flatMapGroup[D2, E2](f: (D, E) => Iterable[Grouping[D2, E2]]) =
    new Reduce[A, B, C, D2, E2](m, {
      (k, v) => r(k, v).flatMap { case (k2, v2) => f(k2, v2) }
    })
  //XXX
  //    new Reduce[A, B, C, D2, E2](m, Function.untupled(r.tupled >>> { _.flatMap(f.tupled) }))

}

object Reduce {
  implicit def reduce2FunctorWithTraversableValues[A, B, C, D, E](reduce: Reduce[A, B, C, D, Iterable[E]]) =
    new Reduce[A, B, C, D, Iterable[E]](reduce.m, reduce.r) with FunctorWithTraversableValuesLike[E] {

      override type OutFunctorOnAssociativeConmutative[E2] = Reduce[A, B, C, D, E2]

      override def genericCountValues[N: Numeric](f: E => Boolean) = map { x => fromInt(x.count(f)) }

      protected override def mapOnAssociativeConmutative(f: Iterable[E] => E) = map(f)
    }
}