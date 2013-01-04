package com.github.yahd.builder.state.generic
import com.github.yahd.Prelude._

trait FunctorWithTraversableValuesLike[A] extends FunctorLike[Iterable[A]] {

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