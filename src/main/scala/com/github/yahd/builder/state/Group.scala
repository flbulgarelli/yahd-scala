package com.github.yahd.builder.state
import com.github.yahd.Yahd._


class Group[A, B, C](m: MFunction[A, B, C])
  extends TerminalLike[A, B, C, Nothing, Nothing] {

  def mapValues[D](f: Iterable[C] => D) = mapEntries { (k, vs) => (k, f(vs)) }
  def mapKeys[D](f: B => D) = mapEntries { (k, vs) => (f(k), vs) }
  def mapEntries[D, E](f: (B, Iterable[C]) => (D, E)) =
    new Reduce[A, B, C, D, E](m, { (k, vs) => Iterable(f(k, vs)) })

  def mapValuesFolding[D](initial: D)(f: (D, C) => D) =
    mapValues(_.foldLeft(initial)(f))

  def combineWith[D] = mapValuesFolding[D] _

  def mapValuesReducing(f: (C, C) => C) = mapValues(_.reduce(f))

  def combine = mapValuesReducing _

  def mapValuesLength = mapValuesFolding(0) { (x, y) => x + 1 }

  def combineLength = mapValuesLength

  override def mcr = (m, None, None)
}

object Group {
  
  implicit def grouperMCRStream2numericGrouperMCRStream[A, B, C](stream: Group[A, B, C])(implicit n: Numeric[C]) =
    new Object {
      def mapValuesSum = stream.combine(n.plus(_, _))
      def combineSum = mapValuesSum
    }
}
