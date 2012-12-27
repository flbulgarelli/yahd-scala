package com.github.yahd.builder.state
import com.github.yahd.Yahd._
import com.github.yahd.MCR
import com.github.yahd.Prelude._
//FIXME support combiners
class Group[A, B, C](m: MFunction[A, B, C]) {

  //Pair-oriented
  def mapPairs[D, E](f: (B, Iterable[C]) => (D, E)) =
    flatMapPairs { (k, vs) => List(f(k, vs)) }
 
  def filterPairs[D, E](f: (B, Iterable[C]) => Boolean) =
    flatMapPairs { (k, vs) => if (f(k, vs)) List((k, vs)) else Nil } 
    
  def flatMapPairs[D, E](f: (B, Iterable[C]) => Iterable[(D, E)]) =
    new Reduce[A, B, C, D, E](m, { (k, vs) => f(k, vs) })    
  
  //Value-oriented
  
  private def mapPairsOnValue[D](f: Iterable[C] => D) = mapPairs { onValue(f) }

  def mapValues[D](f: C => D) =
    mapPairsOnValue(_.map(f))
    
  def filterValues(f: C => Boolean) =
    mapPairsOnValue(_.filter(f))
  
  def foldValues[D](initial: D)(f: (D, C) => D) =
    mapPairsOnValue(_.foldLeft(initial)(f))

  def reduceValues(f: (C, C) => C) = mapPairsOnValue(_.reduce(f))
  
  def combineValues = reduceValues _

  def maxValuesBy[D](f:C => D)(implicit n: Ordering[D]) = mapPairsOnValue(_.maxBy(f))
  
  def minValuesBy[D](f:C => D)(implicit n: Ordering[D]) = mapPairsOnValue(_.minBy(f))

  def lengthValues = new Group[A, B, Int](m >>> (_.map { case (x, y) => (x, 1) })).mapPairsOnValue(_.sum)
  
  def countValues(f: C => Boolean) = mapPairsOnValue(_.count(f))
}

object Group {

  implicit def group2NumericGroup[A, B, C](state: Group[A, B, C])(implicit n: Numeric[C]) =
    new Object {
      def sumValues = state.mapPairsOnValue(_.sum)
    }

  implicit def group2OrderedGroup[A, B, C](state: Group[A, B, C])(implicit n: Ordering[C]) =
    new Object {
      def maxValues = state.mapPairsOnValue(_.max)
      def minValues = state.mapPairsOnValue(_.min)
    }
}
