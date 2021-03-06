package com.github.yahd.builder.state
import com.github.yahd.Yahd.MFunction

class Initial[A] extends MapLike[A, A] {

  override def flatMap[B](f: A => Traversable[B]): Map[A, B] =
    new Map[A, B](f)

  def m[B, C](m: MFunction[A, B, C]) = new Group[A, B, C](m)

}

