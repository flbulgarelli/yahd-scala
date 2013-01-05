package com.github.yahd.builder.state

import com.github.yahd.{ Yahd, Prelude }
import Yahd._
import Prelude._
import com.github.yahd.M

/**
 * Builder state reached after a flatMap function has being registered
 * @author flbulgarelli
 */
class Map[A, B](private val pm: A => Iterable[B]) extends MapLike[A, B] {

  override def flatMap[C](f: B => Iterable[C]) =
    new Map[A, C](pm >>> (_.flatMap(f)))

  /**
   * Partitions this stream based on elements equality, that is, answers a partitioned stream 
   * where equal elements are members of the same group.
   * 
   * This computation is analogous to [[TraversableOnce#groupBy]], 
   * using the identity function.
   * 
   * For example, if you have a stream of strings with elements ["foo", "bar", "foo" and "foobar"], the following code:
   * {{{
   *    strings.group   
   * }}}
   * 
   * will return the partitioned stream [("bar", ["bar"]),("foo", ["foo", "foo"]), ("foobar", ["foobar"])]  
   * 
   * Equivalent to: 
   * 
   * {{{
   *   groupOn(Prelude.id)
   * }}}
   * @return 
   */
  def group = groupOn(id)

  /**
   * Partitions this stream of elements using the given group function as discriminator.
   * This computation is analogous to [[TraversableOnce#groupBy]].
   *
   * For example, let suppose a stream of log entries, where, among other information,
   * each one provides its size in KB and the event hour; and we are interested in the average
   * size of log entries per hour.
   *
   * So, in order to group them by hour, we could do the following:
   *
   * {{{
   *    logEntries.groupOn(_.getHour)
   * }}}
   *
   * That way, we are converting a stream of log entries, into a partitioned stream, where keys are hours,
   * and values are traversables of log entries
   *
   * @param g the group function
   */
  def groupOn[C](f: B => C) =
    groupMappingOn(f)(id)

  def groupMapping[C](f: B => C) =
    groupMappingOn(id)(f)

  /**
   * Groups elements in the stream using [[Map#groupOn]], 
   * and maps values in each group using the given mapping function g
   * 
   * Equivalent to:
   * {{{
   *    groupOn(f).mapValuesUsingMapper(g)
   * }}}
   * 
   * @param f the grouping function
   * @param g the mapping function
   */
  def groupMappingOn[C, D](f: B => C)(g: B => D) =
    new Group[A, C, D](pm >>> (_.map { x => (f(x), g(x)) }))

}

object Map {
  /**Implicit conversion for treating a Map  builder state as terminal, when its value type is a pair*/
  implicit def mapToTerminal[A, B, C](mapState: Map[A, (B, C)]) = new TerminalLike[A, B, C, B, C] {
    override def mcr = M(mapState.pm)
  }
}