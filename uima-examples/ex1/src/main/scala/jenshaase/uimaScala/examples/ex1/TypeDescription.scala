/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.examples.ex1

import jenshaase.uimaScala.core.description._
import UimaTyp._

class TypeDescription extends TypeSystemDescription {

  def name = "TutorialTypeSystem"
  def basePackage = "jenshaase.uimaScala.examples.ex1.types"

  override def description =
    Some("Type System Definition for the tutorial examples - as of Exercise 1")

  def types = Seq(
    "RoomNumber" extend UimaAnnotation
      features (StringFeature("building", Some("building containing this room"))))
}