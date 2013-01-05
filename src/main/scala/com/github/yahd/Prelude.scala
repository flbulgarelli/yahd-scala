package com.github.yahd

object Prelude {

  type JavaIterable[A] = java.lang.Iterable[A]

  def id[A] = { x: A => x }
  def const[A, B](x: B) = { _: A => x }

  type Id[A] = A
  type Functor[A[_], B] = A[B]
  
  def fromInt[N](int : Int)(implicit n : Numeric[N]) = n.fromInt(int)
  
  def genericOne[N : Numeric] = implicitly[Numeric[N]].one

  implicit def function2Composable[A, B](f:A => B) = new AnyRef {
    def >>>[C] = (g: B => C) => f.andThen(g)
    def o [C] = (g: C => A) => f.compose(g) 
  }  
  
  type Grouping[A, B] = (A, B) 
  
  object Grouping {
    
    def onKey[A, B, C](f: B => C) = onKeyValue(f)(id[A])
    def onValue[A, B, C](f: B => C) = onKeyValue(id[A])(f)
    
    def onKeyValue[A, B, C, D](f : A => C)(g: B => D) = (x: A, y:B) => Grouping(f(x), g(y)) 
    
    def genericUnitary[A, N : Numeric](x:A) = Grouping(x, genericOne)
    def unitary[A] = genericUnitary[A, Int] _

    def distribute[A](p1: (A, A), p2: (A, A))(f: (A, A) => A) = (f(p1._1, p2._1), f(p1._2, p2._2))
    
    def apply[A, B](x: A, y: B) : Grouping[A, B] = (x, y)
  }
}