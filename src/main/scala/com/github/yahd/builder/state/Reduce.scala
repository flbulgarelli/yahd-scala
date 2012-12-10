package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.MCR

class Reduce[A, B, C, D, E](m: MFunction[A, B, C], r: RFunction[B, C, D, E])
  extends TerminalLike[A, B, C, D, E] {
  def mcr = MCR(m, None, Some(r))
}
