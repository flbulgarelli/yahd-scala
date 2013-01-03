package com.github.yahd.builder.state
import com.github.yahd.Yahd._
import com.github.yahd.MCR
import com.github.yahd.Prelude._
import scala.collection.generic.FilterMonadic
import com.github.yahd.MonadicLike
import com.github.yahd.MonadicWithKeyLike
import com.github.yahd.FunctorWithTraversableValuesLike

class Group[A, B, C](m: MFunction[A, B, C])
  extends MonadicWithKeyLike[B, Iterable[C]]
  with FunctorWithTraversableValuesLike[C] {

  override type OutFunctorWithKey[D, E] = Reduce[A, B, C, D, E]
  override type OutFunctorOnAssociativeConmutative[C2] = Combine[A, B, C2]

  override def flatMapGroup[D, E](f: (B, Iterable[C]) => Iterable[Grouping[D, E]]) =
    new Reduce[A, B, C, D, E](m, { (k, vs) => f(k, vs) })

  def reduceValuesUsingCombiner(f: (C, C) => C) =
    mapUsingCombiner(_.reduce(f))

  def mapUsingCombiner =
    mapOnAssociativeConmutative _

  protected override def mapOnAssociativeConmutative(f: Iterable[C] => C) =
    new Combine[A, B, C](m, (k, vs) => Grouping(k, f(vs)))

  def flatMapValuesUsingMapper[D](f: C => Iterable[D]) =
    composedGroup[D](_.flatMap { case (x, y) => f(y).map(Grouping(x, _)) })

  def mapValuesUsingMapper[D](f: C => D) =
    composedGroup[D](_.map { case (x, y) => Grouping(x, f(y)) })

  def filterValuesUsingMapper(f: C => Boolean) =
    composedGroup[C](_.filter { case (x, y) => f(y) })

  private def composedGroup[D](g: Iterable[Grouping[B, C]] => Iterable[Grouping[B, D]]) =
    new Group[A, B, D](m >>> g)

  import Grouping.unitary

  override def genericLengthValues[N: Numeric] =
    composedGroup[N](_.map { case (x, y) => unitary(x) }).sumValues

  override def genericCountValues[N: Numeric](f: C => Boolean) =
    composedGroup[N](_.flatMap { case (x, y) => if (f(y)) List(unitary(x)) else Nil }).sumValues

  //TODO distinct, average, count = length?, map as groupMapping?, support list as output, pipeline of reduce
}

