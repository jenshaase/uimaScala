/**
 * Copyright (C) 2011 Jens Haase
 */
package uimascala

object UimaTyp extends Enumeration {
  type UimaTyp = Value

  val TOP = Value("uima.cas.TOP")
  val Integer = Value("uima.cas.Integer")
  val Float = Value("uima.cas.Float")
  val String = Value("uima.cas.String")
  val Byte = Value("uima.cas.Byte")
  val Short = Value("uima.cas.Short")
  val Long = Value("uima.cas.Long")
  val Double = Value("uima.cas.Double")
  val Boolean = Value("uima.cas.Boolean")
  val FSArray = Value("uima.cas.FSArray")
  val IntegerArray = Value("uima.cas.IntegerArray")
  val FloatArray = Value("uima.cas.FloatArray")
  val StringArray = Value("uima.cas.StringArray")
  val BooleanArray = Value("uima.cas.BooleanArray")
  val ByteArray = Value("uima.cas.ByteArray")
  val ShortArray = Value("uima.cas.ShortArray")
  val LongArray = Value("uima.cas.LongArray")
  val DoubleArray = Value("uima.cas.DoubleArray")
  val AnnotationBase = Value("uima.cas.AnnotationBase")
  val Annotation = Value("uima.tcas.Annotation")
  val DocumentAnnotation = Value("uima.tcas.DocumentAnnotation")
  val EmptyFloatList = Value("uima.cas.EmptyFloatList")
  val EmptyFSList = Value("uima.cas.EmptyFSList")
  val EmptyIntegerList = Value("uima.cas.EmptyIntegerList")
  val EmptyStringList = Value("uima.cas.EmptyStringList")
  val FloatList = Value("uima.cas.FloatList")
  val FSList = Value("uima.cas.FSList")
  val IntegerList = Value("uima.cas.IntegerList")
  val StringList = Value("uima.cas.StringList")
  val NonEmptyFloatList = Value("uima.cas.StringList")
  val NonEmptyFSList = Value("uima.cas.NonEmptyFSList")
  val NonEmptyIntegerList = Value("uima.cas.NonEmptyIntegerList")
  val NonEmptyStringList = Value("uima.cas.NonEmptyStringList")
  val Sofa = Value("uima.cas.Sofa")
}

