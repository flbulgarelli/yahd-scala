package com.github.yahd
import com.github.yahd.Prelude.Grouping

trait MonadicLike[A] {
  type This[B] <: MonadicLike[B]

  def map[B](f: A => B) =
    flatMap { x => List(f(x)) }

  def filter(f: A => Boolean) =
    flatMap { x => if (f(x)) List(x) else Nil }

  def flatMap[B](f: A => Iterable[B]): This[B]
}

trait MonadicWithKeyLike[K, V] extends MonadicLike[V] {

  type This[V2] = ThisWithKey[K, V2]
  type ThisWithKey[K2, V2] <: MonadicWithKeyLike[K2, V2]

  /*Default implementation of flatMap*/

  override def flatMap[V2](f: V => Iterable[V2]) =
    flatMapWithKey((x, y) => f(y))

  /*Map With Key*/

  def mapWithKey[V2](f: (K, V) => V2) =
    flatMapWithKey { (x, y) => List(f(x, y)) }

  def filterWithKey(f: (K, V) => Boolean) =
    flatMapWithKey { (x, y) => if (f(x, y)) List(y) else Nil }

  def flatMapWithKey[V2](f: (K, V) => Iterable[V2]): This[V2]

  /*Map Group*/

  def mapGroup[K2, V2](f: (K, V) => Grouping[K2, V2]): ThisWithKey[K2, V2] =
    flatMapGroup { (x, y) => List(f(x, y)) }

  def filterGroup(f: (K, V) => Boolean): ThisWithKey[K, V] =
    flatMapGroup { (x, y) => if (f(x, y)) List(Grouping(x, y)) else Nil }

  def flatMapGroup[K2, V2](f: (K, V) => Iterable[Grouping[K2, V2]]): ThisWithKey[K2, V2]
}

