package com.github.yahd.builder.state.generic
import com.github.yahd.Prelude._

trait PartitionedMonadicWithTraversableValuesLike[K, V] extends PartitionedMonadicLike[K, Traversable[V]]{

  type OutFunctorOnAssociativeConmutative[V2]

  protected def mapOnAssociativeConmutative(f: Traversable[V] => V): OutFunctorOnAssociativeConmutative[V]

 def mapValuesWithKey[V2](f: (K, V) => V2) =
    applyToValuesWithKey(f)(_.map(_))
  
  def filterValuesWithKey(f: (K, V) => Boolean) =
    applyToValuesWithKey(f)(_.filter(_))
  
  def flatMapValuesWithKey[V2](f: (K, V) => Traversable[V2]) =
    applyToValuesWithKey(f)(_.flatMap(_))

  def foldValuesWithKey[B](initial: B)(f: (K, B, V) => B) =
    applyBinaryToValuesWithKey(f)(_.foldLeft(initial)(_))

  def reduceValuesWithKey(f: (K, V, V) => V) =
    applyBinaryToValuesWithKey(f)(_.reduce(_))
    
  private def applyToValuesWithKey[R1, R2](f : (K, V) => R1)(op: (Traversable[V], V => R1) => R2) = 
    mapWithKey { (k, vs) => op(vs, { v => f(k, v) }) }

  private def applyBinaryToValuesWithKey[AV, R1, R2](f : (K, AV, V) => R1)(op: (Traversable[V], (AV, V) => R1) => R2) = 
    mapWithKey { (k, vs) => op(vs, { (a, v) => f(k, a, v) }) }
  
  def flatMapValues[V2](f: V => Traversable[V2]) =
    map(_.flatMap(f))

  def mapValues[B](f: V => B) =
    map(_.map(f))

  def filterValues(f: V => Boolean) =
    map(_.filter(f))

  def foldValues[B](initial: B)(f: (B, V) => B) =
    map(_.foldLeft(initial)(f))

  def reduceValues(f: (V, V) => V) =
    map(_.reduce(f))

  def maxValuesBy[B: Ordering](f: V => B) =
    mapOnAssociativeConmutative(_.maxBy(f))

  def minValuesBy[B: Ordering](f: V => B) =
    mapOnAssociativeConmutative(_.minBy(f))

  def maxValues(implicit o: Ordering[V]) =
    mapOnAssociativeConmutative(_.max)

  def minValues(implicit o: Ordering[V]) =
    mapOnAssociativeConmutative(_.min)

  def sumValues(implicit n: Numeric[V]) =
    mapOnAssociativeConmutative(_.sum)

  def takeValues(n: Int) =
    map(_.take(n))

  def genericLengthValues[N: Numeric] =
    genericCountValues(const(true))

  def genericCountValues[N: Numeric](f: V => Boolean): OutFunctorOnAssociativeConmutative[N]

  def lengthValues =
    genericLengthValues[Int]

  def countValues =
    genericCountValues[Int] _

}