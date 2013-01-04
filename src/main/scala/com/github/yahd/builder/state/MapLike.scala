package com.github.yahd.builder.state

import generic.MonadicLike

trait MapLike[A, B] extends MonadicLike[B] {

  override type OutFunctor[C] = Map[A, C]

}