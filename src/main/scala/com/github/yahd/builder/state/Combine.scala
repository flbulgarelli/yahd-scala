package com.github.yahd.builder.state

import com.github.yahd.Yahd._
import com.github.yahd.MC
import com.github.yahd.MonadicWithKeyLike

class Combine[A, B, C](m: MFunction[A, B, C], c: CFunction[B, C])
  //  extends MonadicWithKeyLike[B, C]
  extends TerminalLike[A, B, C, B, C] {
  //  override type OutFunctorWithKey[D, E] = Reduce[A, B, C, D, E]
  def mcr = MC(m, c)
}
