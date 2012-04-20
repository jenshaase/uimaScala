/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import java.util.Date
import java.io.File
import scala.io.Source
import weka.core.Attribute

trait WekaAttribute {
  val name: String
}

case class NorminalAttribute(val name: String, norminals: Set[String], fallback: String = "XXX") extends WekaAttribute
case class ClassAttribute(val name: String, norminals: Set[String]) extends WekaAttribute

object NorminalAttribute {

  def apply(name: String, norminals: Set[String]): NorminalAttribute =
    apply(name, norminals, "XXX")

  def apply(name: String, file: File): NorminalAttribute =
    apply(name, file, "XXX")

  def apply(name: String, file: File, fallback: String): NorminalAttribute =
    apply(name, Source.fromFile(file).getLines.toSet, fallback)
}

trait NumericAttribute extends WekaAttribute
case class IntAttribute(val name: String) extends NumericAttribute
case class LongAttribute(val name: String) extends NumericAttribute
case class DoubleAttribute(val name: String) extends NumericAttribute
case class FloatAttribute(val name: String) extends NumericAttribute

case class DateAttribute(val name: String, format: String = "yyyy-MM-dd'T'HH:mm:ss") extends WekaAttribute