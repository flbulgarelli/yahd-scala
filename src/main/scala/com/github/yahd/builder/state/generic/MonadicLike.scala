package com.github.yahd.builder.state.generic

import com.github.yahd.Prelude._

trait MonadicLike[A] extends FunctorLike[A] {

  override def map[B](f: A => B) =
    flatMap { x => List(f(x)) }

  def filter(f: A => Boolean) =
    flatMap { x => if (f(x)) List(x) else Nil }
  
  def flatMap[B](f: A => Iterable[B]): OutFunctor[B]
}


