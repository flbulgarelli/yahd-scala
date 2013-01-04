package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.MCR

trait TerminalLike[A, B, C, D, E] {
  def mcr: MCR[A, B, C, D, E]
}