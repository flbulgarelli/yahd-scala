package com.github.yahd

object Prelude {

  type JavaIterable[A] = java.lang.Iterable[A]

  def id[A] = { x: A => x }
  def const[A, B](x: B) = { _: A => x }

  def onFirst[A, B, C](f: B => C) = (x: B, y: A) => (f(x), y)
  def onSecond[A, B, C](f: B => C) = (x: A, y: B) => (x, f(y))

  implicit def function2Composable[A, B](f:A => B) = new AnyRef {
    def >>>[C] = (g: B => C) => f.andThen(g)
    def o [C] = (g: C => A) => f.compose(g) 
  }  

}