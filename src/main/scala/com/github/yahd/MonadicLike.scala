package com.github.yahd

import Prelude._

import com.github.yahd.Prelude.Grouping

trait FunctorLike[A] {
  type OutFunctor[B]
  def map[B](f: A => B): OutFunctor[B]
}

trait MonadicLike[A] extends FunctorLike[A] {

  override def map[B](f: A => B) =
    flatMap { x => List(f(x)) }

  def filter(f: A => Boolean) =
    flatMap { x => if (f(x)) List(x) else Nil }

  def flatMap[B](f: A => Iterable[B]): OutFunctor[B]
}

trait MonadicWithKeyLike[K, V] extends MonadicLike[V] {

  override type OutFunctor[V2] = OutFunctorWithKey[K, V2]
  type OutFunctorWithKey[K2, V2]

  /*Default implementation of flatMap*/

  override def flatMap[V2](f: V => Iterable[V2]) =
    flatMapWithKey((x, y) => f(y))

  /*Map With Key*/

  def mapWithKey[V2](f: (K, V) => V2) =
    flatMapWithKey { (x, y) => List(f(x, y)) }

  def filterWithKey(f: (K, V) => Boolean) =
    flatMapWithKey { (x, y) => if (f(x, y)) List(y) else Nil }

  def flatMapWithKey[V2](f: (K, V) => Iterable[V2]) =
    flatMapGroup((x, y) => f(x, y).map(Grouping(x, _)))

  /*Map Group*/

  def mapGroup[K2, V2](f: (K, V) => Grouping[K2, V2]): OutFunctorWithKey[K2, V2] =
    flatMapGroup { (x, y) => List(f(x, y)) }

  def filterGroup(f: (K, V) => Boolean): OutFunctorWithKey[K, V] =
    flatMapGroup { (x, y) => if (f(x, y)) List(Grouping(x, y)) else Nil }

  def flatMapGroup[K2, V2](f: (K, V) => Iterable[Grouping[K2, V2]]): OutFunctorWithKey[K2, V2]
}

trait FunctorWithTraversableValuesLike[K, A] extends MonadicWithKeyLike[K, Iterable[A]] {

  type OutFunctorOnAssociativeConmutative[B]

  protected def mapOnAssociativeConmutative(f: Iterable[A] => A): OutFunctorOnAssociativeConmutative[A]

  def flatMapValues[B](f: A => Iterable[B]) =
    map(_.flatMap(f))

  def mapValues[B](f: A => B) =
    map(_.map(f))

  def filterValues(f: A => Boolean) =
    map(_.filter(f))

  def foldValues[B](initial: B)(f: (B, A) => B) =
    map(_.foldLeft(initial)(f))

  def reduceValues(f: (A, A) => A) =
    map(_.reduce(f))

  def maxValuesBy[B: Ordering](f: A => B) =
    mapOnAssociativeConmutative(_.maxBy(f))

  def minValuesBy[B: Ordering](f: A => B) =
    mapOnAssociativeConmutative(_.minBy(f))

  def maxValues(implicit o: Ordering[A]) =
    mapOnAssociativeConmutative(_.max)

  def minValues(implicit o: Ordering[A]) =
    mapOnAssociativeConmutative(_.min)

  def sumValues(implicit n: Numeric[A]) =
    mapOnAssociativeConmutative(_.sum)

  def takeValues(n: Int) =
    map(_.take(n))

  def genericLengthValues[N: Numeric] =
    genericCountValues(const(true))

  def genericCountValues[N: Numeric](f: A => Boolean): OutFunctorOnAssociativeConmutative[N]

  def lengthValues =
    genericLengthValues[Int]

  def countValues =
    genericCountValues[Int] _

}
