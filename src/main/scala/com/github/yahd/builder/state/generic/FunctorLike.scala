package com.github.yahd.builder.state.generic

trait FunctorLike[A] {
  type OutFunctor[B]
  def map[B](f: A => B): OutFunctor[B]
}