package com.github.yahd.builder.state


//FIXME make arguments variant
class Initial[A] extends ConcatMapLike[A] {
  
  //XXX avoid overriding
  override def map[B](f: A => B) =
    super.map(f).asInstanceOf[Map[A, B]]

  override def filter(f: A => Boolean) =
    super.filter(f).asInstanceOf[Map[A, A]]

  def concatMap[B](f: A => Iterable[B]) : Map[A, B] =
    new Map[A, B](f)
}

