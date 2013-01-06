package com.github.yahd.builder.state
import com.github.yahd.Prelude._
import com.github.yahd.Yahd._
import generic.PartitionedMonadicLike
import generic.PartitionedMonadicWithTraversableValuesLike

abstract class AbstractReduce[A, B, C, D, E](private val m: MFunction[A, B, C], private val r: RFunction[B, C, D, E])
  extends PartitionedMonadicLike[D, E]
  with TerminalLike[A, B, C, D, E] {

  override def flatMapGroup[D2, E2](f: (D, E) => Traversable[Grouping[D2, E2]]) =
    newReduce {
      (k, v) => r(k, v).flatMap { case (k2, v2) => f(k2, v2) }
    }

  protected def newReduce[D2, E2](r: RFunction[B, C, D2, E2]): OutFunctorWithKey[D2, E2]
}

abstract class AbstractReduceWithTraversableValuesLike[K, V] extends PartitionedMonadicWithTraversableValuesLike[K, V] {

  override final type OutFunctorOnAssociativeConmutative[V2] = OutFunctorWithKey[K, V2]

  override def genericCountValues[N: Numeric](f: V => Boolean) = map { x => fromInt(x.count(f)) }

  protected override def mapOnAssociativeConmutative(f: Traversable[V] => V) = map(f)

  def averageValues(implicit n: Numeric[V]) = {
    import n._
    map { x => x.sum.toDouble / x.size.toDouble }
  }
}
