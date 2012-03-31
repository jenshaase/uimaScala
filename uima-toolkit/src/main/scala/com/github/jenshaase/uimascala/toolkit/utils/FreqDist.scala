/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.utils

import scala.collection.MapProxy

class FreqDist[T](val elems: Iterable[T]) extends MapProxy[T, Int] {

  def this() = this(Seq.empty)

  def self = elems.map { e ⇒ (e, 1) }.groupBy(_._1).map { kv ⇒
    (kv._1, kv._2.size)
  }

  def inc(key: T, count: Int = 1) =
    self + (key -> (getOrElse(key, 0) + count))

  def hapaxes = filter(_._2 == 1).map(_._1)

  def freq(key: T) = getOrElse(key, 0)

  def sorted = toList.sortBy(_._2)(Ordering[Int].reverse)
}

object FreqDist {
  def empty[T] = new FreqDist[T]()

  def apply[T](elems: T*) = new FreqDist[T](elems)
}