package com.github.yahd.builder.state
import com.github.yahd.Prelude.Grouping
import scala.collection.generic.CanBuildFrom
import com.github.yahd.MonadicLike

trait MapLike[A, B] extends MonadicLike[B] {

  override type OutFunctor[C] = Map[A, C]

}