/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.description

import UimaTyp._
import xml.Node

abstract class Feature(
    name: String,
    rangeType: String,
    desc: Option[String] = None,
    elementType: Option[String] = None,
    multipleRef: Option[Boolean] = None) {

  def toXml: Node =
    <featureDescription>
      <name>{ name }</name>
      { desc map (x ⇒ <description>{ x }</description>) flatten }
      <rangeTypeName>{ rangeType }</rangeTypeName>
      { elementType map (x ⇒ <elementType>{ x }</elementType>) flatten }
      { multipleRef map (x ⇒ <multipleReferencesAllowed>{ x }</multipleReferencesAllowed>) flatten }
    </featureDescription>
}

case class TopFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaTOP, desc)

case class IntegerFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaInt, desc)

case class FloatFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaFloat, desc)

case class StringFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaString, desc)

case class ByteFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaByte, desc)

case class ShortFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaShort, desc)

case class LongFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaLong, desc)

case class DoubleFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaDouble, desc)

case class BooleanFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaBoolean, desc)

case class FSArrayFeature(name: String, elemType: String, desc: Option[String] = None)
  extends Feature(name, UimaFSArray, desc, Some(elemType))

case class IntArrayFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaIntArray, desc, Some(UimaInt))

case class FloatArrayFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaFloatArray, desc, Some(UimaFloat))

case class StringArrayFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaStringArray, desc, Some(UimaString))

case class BooleanArrayFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaBooleanArray, desc, Some(UimaBoolean))

case class ByteArrayFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaByteArray, desc, Some(UimaByte))

case class ShortArrayFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaShortArray, desc, Some(UimaShort))

case class LongArrayFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaLongArray, desc, Some(UimaLong))

case class DoubleArrayFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaDoubleArray, desc, Some(UimaDouble))

case class AnnotationBaseFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaAnnotationBase, desc)

case class AnnotationFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaAnnotation, desc)

case class DocumentAnnotationFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaDocumentAnnotation, desc)

case class EmptyFloatListFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaEmptyFloatList, desc, Some(UimaFloat))

case class EmptyFSListFeature(name: String, elemType: String, desc: Option[String] = None)
  extends Feature(name, UimaEmptyFSList, desc, Some(elemType))

case class EmptyIntListFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaEmptyIntList, desc, Some(UimaInt))

case class EmptyStringListFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaEmptyStringList, desc, Some(UimaString))

case class FloatListFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaFloatList, desc, Some(UimaFloat))

case class FSListFeature(name: String, elemType: String, desc: Option[String] = None)
  extends Feature(name, UimaFSList, desc, Some(elemType))

case class IntListFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaIntList, desc, Some(UimaInt))

case class StringListFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaStringList, desc, Some(UimaString))

case class NonEmptyFloatListFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaNonEmptyFloatList, desc, Some(UimaFloat))

case class NonEmptyFSListFeature(name: String, elemType: String, desc: Option[String] = None)
  extends Feature(name, UimaNonEmptyFSList, desc, Some(elemType))

case class NonEmptyIntListFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaNonEmptyIntList, desc, Some(UimaInt))

case class NonEmptyStringListFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaNonEmptyStringList, desc, Some(UimaString))

case class SofaFeature(name: String, desc: Option[String] = None)
  extends Feature(name, UimaSofa, desc)