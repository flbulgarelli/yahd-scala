package com.github.yahd.builder.state
import com.github.yahd.Prelude._
import com.github.yahd.Yahd._
import generic.MonadicWithKeyLike
import generic.FunctorWithTraversableValuesLike

abstract class AbstractReduce[A, B, C, D, E](private val m: MFunction[A, B, C], private val r: RFunction[B, C, D, E])
  extends MonadicWithKeyLike[D, E]
  with TerminalLike[A, B, C, D, E] {

  override def flatMapGroup[D2, E2](f: (D, E) => Iterable[Grouping[D2, E2]]) =
    newReduce {
      (k, v) => r(k, v).flatMap { case (k2, v2) => f(k2, v2) }
    }

  protected def newReduce[D2, E2](r: RFunction[B, C, D2, E2]): OutFunctorWithKey[D2, E2]
  //XXX
  //    new Reduce[A, B, C, D2, E2](m, Function.untupled(r.tupled >>> { _.flatMap(f.tupled) }))

}

abstract class AbstractReduceWithTraversableValuesLike[E] extends FunctorWithTraversableValuesLike[E] {

  override type OutFunctor[E2] = OutFunctorOnAssociativeConmutative[E2]

  override def genericCountValues[N: Numeric](f: E => Boolean) = map { x => fromInt(x.count(f)) }

  protected override def mapOnAssociativeConmutative(f: Iterable[E] => E) = map(f)
}
