/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import scala.xml.{ XML, PrettyPrinter, Node }
import java.io.{ File, FileWriter, BufferedWriter }
import weka.core.{ Instances, Attribute }
import com.github.jenshaase.uimascala.toolkit.ml.Feature
import com.github.jenshaase.uimascala.toolkit.ml.weka.WekaConverter._
import scala.collection.JavaConversions._

class Meta(val relation: String, val attributes: Seq[WekaAttribute]) {

  val attributeIndex = attributes.map { attr ⇒
    attr.name -> attr
  }.toMap

  val norminalIndex = attributes.collect {
    case n: NorminalAttribute ⇒ n.name -> n
  }.toMap

  lazy val createInstances = {
    val inst = new Instances(relation, new java.util.ArrayList[Attribute](attributes.map(convertAttribute _)), 0)

    classIndex match {
      case Some(index) ⇒ inst.setClassIndex(index)
      case _           ⇒
    }

    inst
  }

  def classAttribute = attributes.filter {
    case ClassAttribute(_, _) ⇒ true
    case _                    ⇒ false
  } head

  def classIndex = attributes.zipWithIndex.filter {
    case (ClassAttribute(_, _), _) ⇒ true
    case _                         ⇒ false
  }.headOption.map(_._2)

  def toXml =
    <wekameta>
      <relation>{ relation }</relation>
      <attributes>
        { for (att ← attributes) yield attToXml(att) }
      </attributes>
    </wekameta>

  private def attToXml(att: WekaAttribute) = att match {
    case NorminalAttribute(name, norminals, fallback) ⇒
      <attribute type={ "norminal" }>
        <name>{ name }</name>
        <norminals>{ for (n ← norminals) yield <norminal>{ n }</norminal> }</norminals>
        <fallback>{ fallback }</fallback>
      </attribute>
    case DateAttribute(name, format) ⇒
      <attribute type={ "date" }>
        <name>{ name }</name>
        <format>{ format }</format>
      </attribute>
    case IntAttribute(name) ⇒
      <attribute type={ "int" }>
        <name>{ name }</name>
      </attribute>
    case LongAttribute(name) ⇒
      <attribute type={ "long" }>
        <name>{ name }</name>
      </attribute>
    case DoubleAttribute(name) ⇒
      <attribute type={ "double" }>
        <name>{ name }</name>
      </attribute>
    case FloatAttribute(name) ⇒
      <attribute type={ "float" }>
        <name>{ name }</name>
      </attribute>
    case ClassAttribute(name, norminals) ⇒
      <attribute type={ "class" }>
        <name>{ name }</name>
        <norminals>{ for (n ← norminals) yield <norminal>{ n }</norminal> }</norminals>
      </attribute>
  }
}

object Meta {
  def apply(aRelation: String, aAttributes: Seq[WekaAttribute]) =
    new Meta(aRelation, aAttributes)

  def save(file: String, meta: Meta) = {
    val s = new StringBuilder()
    new PrettyPrinter(100, 2).format(meta.toXml, s)

    val writer = new BufferedWriter(new FileWriter(file))
    writer.write(s.toString)
    writer.close()
  }

  def load(file: String): Meta = {
    val xml = XML.loadFile(file)

    new Meta((xml \\ "relation").text,
      (xml \\ "attributes" \ "attribute").map(xmlToAtt _))
  }

  private def xmlToAtt(node: Node) = {
    val typ = (node \ "@type").text
    val name = (node \\ "name").text

    typ match {
      case "norminal" ⇒ NorminalAttribute(name, (node \\ "norminal").map(_.text).toSet, (node \\ "fallback").text)
      case "class"    ⇒ ClassAttribute(name, (node \\ "norminal").map(_.text).toSet)
      case "date"     ⇒ DateAttribute(name, (node \\ "format").text)
      case "int"      ⇒ IntAttribute(name)
      case "long"     ⇒ LongAttribute(name)
      case "double"   ⇒ DoubleAttribute(name)
      case "float"    ⇒ FloatAttribute(name)
    }
  }
}