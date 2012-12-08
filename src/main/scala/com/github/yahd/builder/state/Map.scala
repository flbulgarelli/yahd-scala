package com.github.yahd.builder.state

import com.github.yahd.Yahd._

class Map[A, B](pm: A => Iterable[B]) extends ConcatMapLike[B] {

  override def map[C](f: B => C) =
    super.map(f).asInstanceOf[Map[A, C]]

  override def filter(f: B => Boolean) =
    super.filter(f).asInstanceOf[Map[A, B]]

  override def concatMap[C](f: B => Iterable[C]): ConcatMapLike[C] =
    new Map[A, C](
      pm.andThen(_.flatMap(f)))

  def group = groupMappingOn(id)(id)

  def groupMapping[C](mappingFunction: B => C) =
    groupMappingOn(id)(mappingFunction)

  def groupOn[C](grouppingFunction: B => C) =
    groupMappingOn(grouppingFunction)(id)

  def groupMappingOn[C, D](g: B => C)(m: B => D) =
    new Group[A, C, D](pm.andThen(_.map { x => (g(x), m(x)) }))
}