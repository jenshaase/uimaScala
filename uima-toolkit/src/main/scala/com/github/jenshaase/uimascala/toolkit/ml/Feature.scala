/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml

trait Feature[T] {
  val name: String
  val value: T
}