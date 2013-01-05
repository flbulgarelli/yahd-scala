package com.github.yahd.builder.state.generic
import com.github.yahd.Prelude.Grouping

trait MonadicWithKeyLike[K, V] extends MonadicLike[V] {

  override type OutFunctor[V2] = OutFunctorWithKey[K, V2]
  type OutFunctorWithKey[K2, V2]

  /*Default implementation of flatMap*/

  override def flatMap[V2](f: V => Traversable[V2]) =
    flatMapWithKey((x, y) => f(y))

  /*Map With Key*/

  def mapWithKey[V2](f: (K, V) => V2) =
    flatMapWithKey { (x, y) => List(f(x, y)) }

  def filterWithKey(f: (K, V) => Boolean) =
    flatMapWithKey { (x, y) => if (f(x, y)) List(y) else Nil }

  def flatMapWithKey[V2](f: (K, V) => Traversable[V2]) =
    flatMapGroup((x, y) => f(x, y).map(Grouping(x, _)))

  /*Map Group*/

  def mapGroup[K2, V2](f: (K, V) => Grouping[K2, V2]): OutFunctorWithKey[K2, V2] =
    flatMapGroup { (x, y) => List(f(x, y)) }

  def filterGroup(f: (K, V) => Boolean): OutFunctorWithKey[K, V] =
    flatMapGroup { (x, y) => if (f(x, y)) List(Grouping(x, y)) else Nil }

  def flatMapGroup[K2, V2](f: (K, V) => Traversable[Grouping[K2, V2]]): OutFunctorWithKey[K2, V2]
}
