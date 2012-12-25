package com.github.yahd.builder.state

trait MapLike[A, B] {
  def map[C](f: B => C) =
    concatMap { x => List(f(x)) }

  def filter(f: B => Boolean) =
    concatMap { x => if (f(x)) List(x) else Nil }

  def concatMap[C](f: B => Iterable[C]): Map[A, C]
}