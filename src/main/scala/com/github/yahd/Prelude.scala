package com.github.yahd

object Prelude {

  type JavaIterable[A] = java.lang.Iterable[A]

  def id[A] = { x: A => x }
  def const[A, B](x: B) = { _: A => x }

  def onKey[A, B, C](f: B => C) = (x: B, y: A) => (f(x), y)
  def onValue[A, B, C](f: B => C) = (x: A, y: B) => (x, f(y))
  
  type Id[A] = A

  implicit def function2Composable[A, B](f:A => B) = new AnyRef {
    def >>>[C] = (g: B => C) => f.andThen(g)
    def o [C] = (g: C => A) => f.compose(g) 
  }  

}