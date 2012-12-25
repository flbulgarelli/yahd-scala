package com.github.yahd.builder.state

trait MapLike[A, B] {
  def map[C](f: B => C) =
    flatMap { x => List(f(x)) }

  def filter(f: B => Boolean) =
    flatMap { x => if (f(x)) List(x) else Nil }

  def flatMap[C](f: B => Iterable[C]): Map[A, C]
}