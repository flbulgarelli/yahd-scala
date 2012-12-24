package com.github.yahd.builder.state

import com.github.yahd.{Yahd, Prelude}
import Yahd._
import Prelude._
import com.github.yahd.MCR

class Map[A, B](val pm: A => Iterable[B]) extends ConcatMapLike[B] {

  override def map[C](f: B => C) =
    super.map(f).asInstanceOf[Map[A, C]] //XXX avoid those casts

  override def filter(f: B => Boolean) =
    super.filter(f).asInstanceOf[Map[A, B]]

  override def concatMap[C](f: B => Iterable[C]) =
    new Map[A, C](pm >>>(_.flatMap(f)))

  def group = groupMappingOn(id)(id)

  def groupMapping[C](mappingFunction: B => C) =
    groupMappingOn(id)(mappingFunction)

  def groupOn[C](grouppingFunction: B => C) =
    groupMappingOn(grouppingFunction)(id)

  def groupMappingOn[C, D](g: B => C)(m: B => D) =
    new Group[A, C, D](pm >>> (_.map { x => (g(x), m(x)) }))
    
    
}


object Map {
  implicit def mapToTerminal[A, B, C](mapState : Map[A, (B, C)]) = new TerminalLike[A, B, C, B, C] {
    override def mcr = MCR(mapState.pm, None, None)
  }  
}