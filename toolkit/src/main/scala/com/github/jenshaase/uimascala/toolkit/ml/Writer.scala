/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml

trait Writer {
  def write(features: Iterable[Feature[_]], outcome: Feature[_])

  def finish()
}

trait WriterFactory {
  def newInstance: Writer
}