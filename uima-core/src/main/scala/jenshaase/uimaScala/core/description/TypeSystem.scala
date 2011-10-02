/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.description

import UimaTyp._
import jenshaase.uimaScala.core.XmlDescriptor
import xml.Node

trait TypeSystemDescription extends XmlDescriptor {

  implicit def stringToTyp(s: String) = {
    s.split("\\.").reverse.toList match {
      case x :: Nil ⇒ new Typ(x, basePackage)
      case x :: xs  ⇒ new Typ(x, xs.foldLeft("")(_ + "." + _))
      case Nil      ⇒ new Typ(s, "")
    }
  }

  def name: String
  def basePackage: String
  def types: Seq[Typ]

  def description: Option[String] = None
  def version: Option[String] = None
  def vendor: Option[String] = None
  def imports: Option[Seq[Import]] = None

  def xmlType = "types"
  def toXml: Node =
    <typeSystemDescription xmlns="http://uima.apache.org/resourceSpecifier">
      <name>{ name }</name>
      <description>{ description.getOrElse("") }</description>
      <version>{ version.getOrElse("") }</version>
      <vendor>{ vendor.getOrElse("") }</vendor>
      <imports>
        {
          for (imp ← imports.getOrElse(Nil)) yield imp.toXml
        }
      </imports>
      <types>
        {
          for (t ← types) yield t.toXml
        }
      </types>
    </typeSystemDescription>
}

abstract class Import(value: String) {
  def toXml: Node
}
case class Location(value: String) extends Import(value) {
  def toXml: Node = <import location="{ value }"></import>
}
case class Name(value: String) extends Import(value) {
  def toXml: Node = <import name="{ value }"></import>
}

case class Typ(name: String, basePackage: String) {
  private var _supertype: String = ""
  private var _features: Seq[Feature] = Nil

  def extend(supertype: String) = {
    _supertype = supertype
    this
  }

  def features(features: Feature*) = {
    _features = features
    this
  }

  def toXml: Node =
    <typeDescription>
      <name>{ basePackage + "." + name }</name>
      <description></description>
      <supertypeName>{ _supertype }</supertypeName>
      <features>
        {
          for (f ← _features) yield f.toXml
        }
      </features>
    </typeDescription>
}