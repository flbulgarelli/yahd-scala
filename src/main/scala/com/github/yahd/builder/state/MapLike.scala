package com.github.yahd.builder.state
import com.github.yahd.Prelude.Grouping
import scala.collection.generic.CanBuildFrom
import com.github.yahd.MonadicLike

trait MapLike[A, B] extends MonadicLike[B] {

  type This[C] = Map[A, C]

}