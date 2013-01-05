package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.MCR

/**
 * A type whose instances provide an [[MCR]]
 * configuration.
 *
 * Implementors are builder states that describe a well-formed Hadoop
 * MapReduce Job. For example, in the following builder
 *
 * {{{
 *   _.map(x => x + 1)
 * }}}
 *
 * the state it arrives to is a Map state, which is not capable of providing a proper
 * Job (map computation does not describe even a complete M computation).
 *
 * However, the following builder
 *
 * {{{
 *   _.map(x => x + 1).groupOn(_ % 10).lengthValues
 * }}}
 *
 * does define a proper MapReduce Job, as an MC computation can build from it. Thus, state
 * returned by lengthValues in this case is TerminalLike.
 *
 * @author flbulgarelli
 */
trait TerminalLike[A, B, C, D, E] {

  /**
   * Answers the MCR computation defined
   * by this TerminalLike
   */
  def mcr: MCR[A, B, C, D, E]
}