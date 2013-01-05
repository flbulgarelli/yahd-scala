package com.github.yahd.builder.state
import com.github.yahd.Yahd._
import com.github.yahd.MCR
import com.github.yahd.Prelude._
import scala.collection.generic.FilterMonadic

import generic._
class Group[A, B, C](m: MFunction[A, B, C])
  extends MonadicWithKeyLike[B, Iterable[C]]
  with FunctorWithTraversableValuesLike[C] {

  override type OutFunctorWithKey[D, E] = Reduce[A, B, C, D, E]
  override type OutFunctorOnAssociativeConmutative[C2] = Combine[A, B, C2]

  /*  ========================== */
  /*  Monadic with Keys Protocol */
  /*  ========================== */

  override def flatMapGroup[D, E](f: (B, Iterable[C]) => Iterable[Grouping[D, E]]) =
    r { (k, vs) => f(k, vs) }

  /*  ===================================== */
  /*  FunctorWithTraversableValues Protocol */
  /*  ===================================== */
  
  import Grouping._
  protected override def mapOnAssociativeConmutative(f: Iterable[C] => C) =
    c { (k, vs) => Grouping(k, f(vs)) }

  override def genericLengthValues[N: Numeric] =
    mapValuesUsingMapper[N] { x => genericOne }.sumValues

  override def genericCountValues[N: Numeric](f: C => Boolean) =
    flatMapValuesUsingMapper[N] { x => if (f(x)) List(genericOne) else Nil }.sumValues

  /*  ===========================  */
  /*  Group specific optimizations */
  /*  ===========================  */

  def reduceValuesUsingCombiner(f: (C, C) => C) =
    mapUsingCombiner(_.reduce(f))

  /**
   * Restricted variant of [[Group#map]], that performs the computation
   * inside a Hadoop Combiner. 
   * 
   * ''Warning: even when given function type - input and output type - is compatible with  
   * Hadoop's Combiner, it may produce wrong results if the 
   * given operation is not both commutative and associative. Although this is usually 
   * a good optimization, please read Hadoop documentation before using this computation'' 
   */
  def mapUsingCombiner =
    mapOnAssociativeConmutative _

  def flatMapValuesUsingMapper[D](f: C => Iterable[D]) =
    composedGroup[D](_.flatMap { case (x, y) => f(y).map(Grouping(x, _)) })

  def mapValuesUsingMapper[D](f: C => D) =
    composedGroup[D](_.map { case (x, y) => Grouping(x, f(y)) })

  def filterValuesUsingMapper(f: C => Boolean) =
    composedGroup[C](_.filter { case (x, y) => f(y) })

  /*  ================  */
  /*  Out-Of-Protocol   */
  /*  ================  */

  /**Computes the average of values per group*/
  def averageValues(implicit n: Numeric[C]) = {
    import n._
    mapValuesUsingMapper { x => genericUnitary(x) }
      .reduceValuesUsingCombiner { (value, count) => distribute(value, count)(_ + _) }
      .map(result => result._1.toDouble / result._2.toDouble)
  }

  /*  ==================  */
  /*  Private Primitives  */
  /*  ==================  */

  /**implements the given computation inside a group state*/
  private def composedGroup[D](g: Iterable[Grouping[B, C]] => Iterable[Grouping[B, D]]) =
    new Group[A, B, D](m >>> g)

  /*  ==============  */
  /*  MCR Primitives  */
  /*  ==============  */

  /**Primitive C computation*/
  def c(c: CFunction[B, C]) = new Combine[A, B, C](m, c)

  /**Primitive R computation*/
  def r[D, E](r: RFunction[B, C, D, E]) = new Reduce[A, B, C, D, E](m, r)

  //TODO distinct, count = length?, map as groupMapping?, support list as output
}

