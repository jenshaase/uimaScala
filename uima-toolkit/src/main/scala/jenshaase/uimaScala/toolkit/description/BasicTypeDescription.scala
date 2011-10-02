/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.toolkit.description

import jenshaase.uimaScala.core.description._
import UimaTyp._

class BasicTypeDescription extends TypeSystemDescription {

  def name = "uimascalaToolkitBasic"

  override def description = Some("Basic type description for this toolkit")
  override def version = Some("0.3-SNAPSHOT")

  def basePackage = "jenshaase.uimaScala.toolkit.types"

  def types = Seq(
    "DocumentAnnotation" extend UimaAnnotation features (StringFeature("name"), StringFeature("source")),
    "Token" extend UimaAnnotation,
    "Sentence" extend UimaAnnotation,
    "Stopword" extend UimaAnnotation)
}