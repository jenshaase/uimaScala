/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml

trait Classifier {
  def classify(features: Iterable[Feature[_]]): Option[String]
}

trait ClassifierFactory {
  def newInstance: Classifier
}