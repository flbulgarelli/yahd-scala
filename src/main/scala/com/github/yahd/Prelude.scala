package com.github.yahd

/**
 * Common definitions that are not specific to Yahd programs
 * but are often intensively used by them
 */
object Prelude {

  /**Type synonym for java.lang.Iterable */
  type JavaIterable[A] = java.lang.Iterable[A]

  /**The identity function*/
  def id[A] = { x: A => x }
  /**The constant function.
   * @see Function#const*/
  def const[A, B] = Function.const[A, B] _

  /**The identity type */  
  type Id[A] = A

  /**Generic conversion from integer to a numeric value*/
  def fromInt[N](int: Int)(implicit n: Numeric[N]) = n.fromInt(int)

  /**The number 1, but in a generic numeric type representation*/
  def genericOne[N: Numeric] = implicitly[Numeric[N]].one

  /**Implicit conversion for single argument function that
   * adds composition syntact sugar  */
  implicit def function2Composable[A, B](f: A => B) = new AnyRef {
    /**Syntactic sugar for Function#andThen */
    def >>>[C] = (g: B => C) => f.andThen(g)
    /**Syntactic sugar for Function#compose */
    def o[C] = (g: C => A) => f.compose(g)
  }

  type Grouping[A, B] = (A, B)

  object Grouping {

    def onKey[A, B, C](f: B => C) = onKeyValue(f)(id[A])
    def onValue[A, B, C](f: B => C) = onKeyValue(id[A])(f)

    def onKeyValue[A, B, C, D](f: A => C)(g: B => D) = (x: A, y: B) => Grouping(f(x), g(y))

    def genericUnitary[A, N: Numeric](x: A) = Grouping(x, genericOne)
    def unitary[A] = genericUnitary[A, Int] _

    def distribute[A](p1: (A, A), p2: (A, A))(f: (A, A) => A) = (f(p1._1, p2._1), f(p1._2, p2._2))

    def apply[A, B](x: A, y: B): Grouping[A, B] = (x, y)
  }
}