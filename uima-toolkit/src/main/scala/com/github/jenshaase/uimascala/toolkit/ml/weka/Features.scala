/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import com.github.jenshaase.uimascala.toolkit.ml.Feature
import java.util.Date

case class NorminalFeature(val name: String, val value: String) extends Feature[String]
case class DateFeature(val name: String, val value: Date) extends Feature[Date]

case class DoubleFeature(val name: String, val value: Double) extends Feature[Double]
case class FloatFeature(val name: String, val value: Float) extends Feature[Float]
case class IntFeature(val name: String, val value: Int) extends Feature[Int]
case class LongFeature(val name: String, val value: Long) extends Feature[Long]