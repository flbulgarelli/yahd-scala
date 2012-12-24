package com.github.yahd.builder.state
import com.github.yahd.Yahd._
import com.github.yahd.MCR
import com.github.yahd.Prelude._
//FIXME support combiners
class Group[A, B, C](m: MFunction[A, B, C]) {
  
  def mapValues[D](f: Iterable[C] => D) = mapEntries { onSecond(f) }
  def mapKeys[D](f: B => D) = mapEntries { onFirst(f) }
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

  def mapValuesLength = new Group[A, B, Int](m >>> (_.map { case (x, y) => (x, 1) })).mapValues(_.sum)
  def combineLength = mapValuesLength

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
