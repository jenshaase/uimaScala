/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.examples.ex1

import com.github.jenshaase.uimascala.core.description._
import UimaTyp._

class TypeDescription extends TypeSystemDescription {

  def name = "TutorialTypeSystem"
  def basePackage = "com.github.jenshaase.uimascala.examples.ex1.types"

  override def description =
    Some("Type System Definition for the tutorial examples - as of Exercise 1")

  def types = Seq(
    "RoomNumber" extend UimaAnnotation
      features (StringFeature("building", Some("building containing this room"))))
}