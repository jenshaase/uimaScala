/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import java.util.Date

trait WekaFeature[T] {
  val name: String
  val value: T
}

case class NorminalFeature(val name: String, val value: String) extends WekaFeature[String]
case class DateFeature(val name: String, val value: Date) extends WekaFeature[Date]

case class DoubleFeature(val name: String, val value: Double) extends WekaFeature[Double]
case class FloatFeature(val name: String, val value: Float) extends WekaFeature[Float]
case class IntFeature(val name: String, val value: Int) extends WekaFeature[Int]
case class LongFeature(val name: String, val value: Long) extends WekaFeature[Long]