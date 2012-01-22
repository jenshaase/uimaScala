/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.description

import com.github.jenshaase.uimascala.core.description._
import UimaTyp._

class BasicTypeDescription extends TypeSystemDescription {

  def name = "uimascalaToolkitBasic"

  override def description = Some("Basic type description for this toolkit")
  override def version = Some("0.3-SNAPSHOT")

  def basePackage = "com.github.jenshaase.uimascala.toolkit.types"

  def types = Seq(
    "DocumentAnnotation" extend UimaAnnotation features (StringFeature("name"), StringFeature("source")),
    "Token" extend UimaAnnotation features (StringFeature("POS"), StringFeature("lemma"), StringFeature("stem")),
    "Sentence" extend UimaAnnotation,
    "Stopword" extend UimaAnnotation)
}