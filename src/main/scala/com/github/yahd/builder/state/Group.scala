package com.github.yahd.builder.state
import com.github.yahd.Yahd._
import com.github.yahd.MCR

//FIXME support combiners
class Group[A, B, C](m: MFunction[A, B, C])
  extends TerminalLike[A, B, C, Nothing, Nothing] {

  def mapValues[D](f: Iterable[C] => D) = mapEntries { (k, vs) => (k, f(vs)) }
  def mapKeys[D](f: B => D) = mapEntries { (k, vs) => (f(k), vs) }
  def mapEntries[D, E](f: (B, Iterable[C]) => (D, E)) =
    new Reduce[A, B, C, D, E](m, { (k, vs) => Iterable(f(k, vs)) })

  def mapValuesFolding[D](initial: D)(f: (D, C) => D) =
    mapValues(_.foldLeft(initial)(f))

  def combineWith[D] = mapValuesFolding[D] _

  def mapValuesReducing(f: (C, C) => C) = mapValues(_.reduce(f))
  def combine = mapValuesReducing _

  def mapValuesMaxBy[D](f:C => D)(implicit n: Ordering[D]) = mapValues(_.maxBy(f))
  def combineMaxBy[D](f: C => D)(implicit n: Ordering[D]) = mapValuesMaxBy(f)
  
  def mapValuesMinBy[D](f:C => D)(implicit n: Ordering[D]) = mapValues(_.minBy(f))
  def combineMinBy[D](f: C => D)(implicit n: Ordering[D]) = mapValuesMinBy(f) 

  def mapValuesLength = mapValuesFolding(0) { (x, y) => x + 1 }
  def combineLength = mapValuesLength

  override def mcr = MCR(m, None, None)
}

object Group {

  implicit def group2NumericGroup[A, B, C](state: Group[A, B, C])(implicit n: Numeric[C]) =
    new Object {
      def mapValuesSum = state.mapValues(_.sum)
      def combineSum = mapValuesSum
    }

  implicit def group2OrderedGroup[A, B, C](state: Group[A, B, C])(implicit n: Ordering[C]) =
    new Object {
      def mapValuesMax = state.mapValues(_.max)
      def combineMax = mapValuesMax

      def mapValuesMin = state.mapValues(_.min)
      def combineMin = mapValuesMin
    }
}
