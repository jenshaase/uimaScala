/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.utils

import scala.collection.MapProxy

class FreqDist[T](val map: Map[T, Int]) extends MapProxy[T, Int] {

  def self = map

  def inc(key: T, count: Int = 1): FreqDist[T] =
    new FreqDist[T](self + (key -> (getOrElse(key, 0) + count)))

  def hapaxes = filter(_._2 == 1).map(_._1)

  def freq(key: T) = getOrElse(key, 0)

  def sorted = toList.sortBy(_._2)(Ordering[Int].reverse)
}

object FreqDist {
  def empty[T] = new FreqDist[T](Map.empty)

  def apply[T](elems: T*) = new FreqDist[T](elems.map { e ⇒ (e, 1) }.groupBy(_._1).map { kv ⇒
    (kv._1, kv._2.size)
  })
}