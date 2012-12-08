package com.github.yahd.builder.state

trait ConcatMapLike[A] {
  def map[B](f: A => B) =
    concatMap { x => List(f(x)) }

  def filter(f: A => Boolean) =
    concatMap { x => if (f(x)) List(x) else Nil }

  def concatMap[B](f: A => Iterable[B]): ConcatMapLike[B]
}