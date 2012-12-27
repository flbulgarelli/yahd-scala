package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.MC

class Combine[A, B, C](m: MFunction[A, B, C], c: CFunction[B, C])
  extends TerminalLike[A, B, C, B, C] {
  def mcr = MC(m, c)
}
