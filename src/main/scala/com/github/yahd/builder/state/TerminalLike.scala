package com.github.yahd.builder.state

import com.github.yahd.Yahd._

trait TerminalLike[A, B, C, D, E] {
  def mcr: (MFunction[A, B, C], Option[CFunction[B, C, B, C]], Option[RFunction[B, C, D, E]])
}