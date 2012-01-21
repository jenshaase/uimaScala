/**
 * Copyright (C) 2011 Jens Haase
 */
package uimascala

import UimaTyp._
import scala.xml._

object UimaTypeSystem {
  def apply(name: String)(mutators: Function1[UimaTypeSystem, Unit]*) = {
    val origin = new UimaTypeSystem(name)
    for (f ← mutators) { f(origin) }
    origin
  }
}

class UimaTypeSystem(val name: String) extends Descriptor {
  private var _description: Option[String] = None
  private var _version: Option[String] = None
  private var _vendor: Option[String] = None
  private var _imports: Option[List[UimaImport]] = None
  private var _types: Option[List[UimaType]] = None

  def description(d: String) = _description = Some(d)
  def version(v: String) = _version = Some(v)
  def vendor(v: String) = _vendor = Some(v)

  def withType(name: String, supertype: String)(mutators: Function1[UimaType, Unit]*): Unit = {
    _types = Some(UimaType(name, supertype)(mutators: _*) :: _types.getOrElse(List()))
  }

  def withType(name: String, supertype: UimaTyp)(mutators: Function1[UimaType, Unit]*): Unit = {
    withType(name, supertype.toString)(mutators: _*)
  }

  def withImport(name: String, location: String) = {
    _imports = Some(UimaImport(name, location) :: _imports.getOrElse(List()))
  }

  def toXml: Node = <typeSystemDescription xmlns="http://uima.apache.org/resourceSpecifier">
                      { surround("name", name) }
                      { surround("description", _description) }
                      { surround("version", _version) }
                      { surround("vendor", _vendor) }
                      { surround("imports", _imports.map(AsXmlSeq(_))) }
                      { surround("types", _types.map(AsXmlSeq(_))) }
                    </typeSystemDescription>
}

object UimaType {
  def apply(name: String, supertype: String)(mutators: Function1[UimaType, Unit]*) = {
    val origin = new UimaType(name, supertype)
    for (f ← mutators) { f(origin) }
    origin
  }
}

class UimaType(val name: String, val supertype: String) extends AsXml {
  private var _description: Option[String] = None
  private var _features: Option[List[UimaFeature]] = None

  def description(d: String) = _description = Some(d)

  def withFeature(name: String, rangeType: String)(mutators: Function1[UimaFeature, Unit]*): Unit = {
    _features = Some(UimaFeature(name, rangeType)(mutators: _*) :: _features.getOrElse(List()))
  }

  def withFeature(name: String, rangeType: UimaTyp)(mutators: Function1[UimaFeature, Unit]*): Unit = {
    withFeature(name, rangeType.toString)(mutators: _*)
  }

  def toXml: NodeSeq = <typeDescription>
                         { surround("name", name) }
                         { surround("description", _description) }
                         { surround("supertypeName", supertype) }
                         { surround("features", _features.map(AsXmlSeq(_))) }
                       </typeDescription>
}

object UimaFeature {
  def apply(name: String, rangeType: String)(mutators: Function1[UimaFeature, Unit]*) = {
    val origin = new UimaFeature(name, rangeType)
    for (f ← mutators) { f(origin) }
    origin
  }
}

class UimaFeature(val name: String, val rangeType: String) extends AsXml {
  private var _description: Option[String] = None
  private var _elementType: Option[String] = None
  private var _multipleReferencesAllowed: Option[Boolean] = None

  def description(d: String) = _description = Some(d)

  def elementType(e: String) = _elementType = Some(e)

  def allowMultipleReferences() = _multipleReferencesAllowed = Some(true)

  def disallowMultipleReferences() = _multipleReferencesAllowed = Some(false)

  def toXml: NodeSeq = <featureDescription>
                         { surround("name", name) }
                         { surround("description", _description) }
                         { surround("rangeTypeName", rangeType) }
                         { surround("elementType", _elementType) }
                         { surround("multipleReferencesAllowed", _multipleReferencesAllowed) }
                       </featureDescription>
}

case class UimaImport(location: String, name: String) extends AsXml {
  def toXml = <import name={ name } location={ location }/>
}

trait AsXml {

  def toXml: NodeSeq

  def surround(name: String, value: Any) = value match {
    case s: String         ⇒ Elem(null, name, null, TopScope, Text(s))
    case Some(s: String)   ⇒ Elem(null, name, null, TopScope, Text(s))
    case Some(l: AsXmlSeq) ⇒ Elem(null, name, null, TopScope, l.toXml: _*)
    case Some(b: Boolean)  ⇒ Elem(null, name, null, TopScope, Text(if (b) "true" else "false"))
    case None              ⇒ ""
  }
}

case class AsXmlSeq(seq: Seq[AsXml]) extends AsXml {
  def toXml = seq.map(_.toXml).reduceLeft(_ ++ _)
}

trait Descriptor extends AsXml {
  def get: Node = toXml.head
}

