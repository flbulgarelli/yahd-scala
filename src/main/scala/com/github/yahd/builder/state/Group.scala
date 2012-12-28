package com.github.yahd.builder.state
import com.github.yahd.Yahd._
import com.github.yahd.MCR
import com.github.yahd.Prelude._
//FIXME support combiners
class Group[A, B, C](m: MFunction[A, B, C]) {

  //Group-oriented
  def mapGroups[D, E](f: (B, Iterable[C]) => Grouping[D, E]) =
    flatMapGroups { (k, vs) => List(f(k, vs)) }
 
  def filterGroups[D, E](f: (B, Iterable[C]) => Boolean) =
    flatMapGroups { (k, vs) => if (f(k, vs)) List(Grouping(k, vs)) else Nil } 
    
  def flatMapGroups[D, E](f: (B, Iterable[C]) => Iterable[Grouping[D, E]]) =
    new Reduce[A, B, C, D, E](m, { (k, vs) => f(k, vs) })    
  
  //Value-oriented
  
  private def mapGroupsOnValueWithReducer[D](f: Iterable[C] => D) = 
    mapGroups { Grouping.onValue(f) }
  
  private def mapGroupsOnValueWithCombiner(f: Iterable[C] => C) = 
    new Combine[A, B, C](m, (k, vs) => Grouping(k, f(vs)) )

  def mapValues[D](f: C => D) =
    mapGroupsOnValueWithReducer(_.map(f))
    
  def filterValues(f: C => Boolean) =
    mapGroupsOnValueWithReducer(_.filter(f))
  
  def foldValues[D](initial: D)(f: (D, C) => D) =
    mapGroupsOnValueWithReducer(_.foldLeft(initial)(f))

  def reduceValues(f: (C, C) => C) =
    mapGroupsOnValueWithReducer(_.reduce(f))
  
  def combineValues(f: (C, C) => C) = 
    mapGroupsOnValueWithCombiner(_.reduce(f))

  def maxValuesBy[D : Ordering](f:C => D) =
    mapGroupsOnValueWithCombiner(_.maxBy(f))
  
  def minValuesBy[D : Ordering](f:C => D) = 
    mapGroupsOnValueWithCombiner(_.minBy(f))
    
   def maxValues(implicit o : Ordering[C]) = 
     mapGroupsOnValueWithCombiner(_.max)
     
   def minValues(implicit o : Ordering[C]) = 
     mapGroupsOnValueWithCombiner(_.min)
  
   def sumValues(implicit n : Numeric[C])  = 
     mapGroupsOnValueWithCombiner(_.sum)
     
  private def composedGroup[D](g: Iterable[Grouping[B, C]] => Iterable[Grouping[B, D]]) =
    new Group[A, B, D](m >>> g)
  
  import Grouping.unitary                                          
  
  def genericLengthValues[N : Numeric] = 
    composedGroup[N](_.map { case (x, y) => unitary(x) }).sumValues
  
  def genericCountValues[N : Numeric](f: C => Boolean) = 
    composedGroup[N](_.flatMap { case (x, y) => if (f(y)) List(unitary(x)) else Nil }).sumValues
  
  def lengthValues = 
    genericLengthValues[Int]
  
  def countValues =
    genericCountValues[Int] _
  
  
  //TODO distinct, average, count = length?, map as groupMapping?, support list as output, flatMapValues, pipeline of reduce
}

