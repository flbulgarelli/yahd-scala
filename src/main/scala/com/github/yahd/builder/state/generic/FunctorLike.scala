package com.github.yahd.builder.state.generic

/**
 * Builder state that can exposes a map computation
 *
 * @author flbulgarelli
 * @tparm A the type of element contained by this functor
 */
trait FunctorLike[A] {
  type OutFunctor[B]

  def map[B](f: A => B): OutFunctor[B]
}