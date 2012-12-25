package com.github.yahd.builder.state

class Initial[A] extends MapLike[A, A] {
  
  def flatMap[B](f: A => Iterable[B]) : Map[A, B] =
    new Map[A, B](f)
}

