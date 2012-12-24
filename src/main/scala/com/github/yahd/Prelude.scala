package com.github.yahd

object Prelude {

  type JavaIterable[A] = java.lang.Iterable[A]

  def id[A] = { x: A => x }
  def const[A, B](x: B) = { _: A => x }

  def onFirst[A, B, C](f: B => C) = (x: B, y: A) => (f(x), y)
  def onSecond[A, B, C](f: B => C) = (x: A, y: B) => (x, f(y))
}