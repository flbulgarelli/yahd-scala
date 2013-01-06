package com.github.yahd.app.registry
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer

class Pool[A] {
  private var values: Buffer[A] = new ArrayBuffer
  private var lastAcquired = -1
  def acquire =
    if (lastAcquired == limit)
      None
    else {
      lastAcquired += 1
      Some(values(lastAcquired))
    }
  private def limit = values.size - 1
  
  def size = values.size

  def +=(element: A) = values += element

}