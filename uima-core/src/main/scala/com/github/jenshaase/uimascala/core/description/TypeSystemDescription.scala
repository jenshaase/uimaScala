/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.description

import scala.xml.{Node, PrettyPrinter}
import java.io.{FileWriter, File}
import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.Context

import org.apache.uima.UIMAFramework
import org.apache.uima.util.{ CasCreationUtils, XMLInputSource }
import org.apache.uima.cas.impl.CASImpl
import org.apache.uima.tools.jcasgen._

class TypeSystemDescription extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro TypeSystemDescriptionGenerator.typeSystem_impl
}

object TypeSystemDescriptionGenerator {

  def typeSystem_impl(c: Context)(annottees: c.Expr[Any]*) = {
    import c.universe._

    def bail(message: String) = c.abort(c.enclosingPosition, message)

    // Get package name of annotation
    // See: https://groups.google.com/forum/#!topic/scala-user/BAFaP1fpVS8
    val freshName = c.fresh(newTermName("Probe$"))
    val q"{ $attributedDef; }" = c.typeCheck(q"{ object $freshName; }")
    val baseName: String = attributedDef.symbol.owner.fullName

    val basePath = {
      val x = this.getClass().getClassLoader().getResource(".").getPath();
      x.substring(0, x.indexOf("target"))
    }

    annottees.map(_.tree) match {
      case List(tree @ q"object $name { ..$body }") =>
        val types = body.collect {
          case q"val $name = Annotation { ..$config }" =>
            val features = config collect {
              case q"val $name = Feature[$typ]" =>
                typ match {
                  case Ident(x: TypeName) => (name, x.decoded, None)
                  case AppliedTypeTree(Ident(collType: TypeName), List(Ident(typ: TypeName))) =>
                    (name, typ.decoded, Some(collType.decoded))
                }
            }
            (name, features)
        }

        // Generate xml
        val xml = getXml(c)(name, baseName, types)
        val filename = basePath + "src/main/resources/desc/types/" + name + ".xml"
        writeXml(filename, xml)

        // Generate java code
        val xmlIS = new XMLInputSource(filename)
        val tsd = UIMAFramework.getXMLParser.parseTypeSystemDescription(xmlIS)
        val cas = CasCreationUtils.createCas(tsd, null, null)
        val jg = new Jg()
        jg.mainForCde(null, new UimaLoggerProgressMonitor(), new LogThrowErrorImpl(),
          filename, basePath + "src/main/java", tsd.getTypes, cas.asInstanceOf[CASImpl])

        // Objects to access Helpers
        val objects = types map { case (name, _) =>
          val path = baseName + "." + name
          q"""val $name = throw new RuntimeException("Please use " +  $path)"""
        }

        c.Expr[Any](
          q"""object $name {
            ..$objects
          }
          """
        )
      case _ => bail(
        "Error. Wrong annotation"
      )
    }
  }

  private val pp = new PrettyPrinter(500, 2)
  def writeXml(file: String, n: Node) = {
    new File(file).getParentFile.mkdirs
    val sb = new StringBuilder()

    pp.format(n, sb)

    val fw = new FileWriter(file)
    fw.write("<?xml version='1.0' encoding='UTF-8'?>\n")
    fw.write(sb.toString)
    fw.write("\n")
    fw.close()
  }

  def getXml(c: Context)(name: c.universe.TermName, baseName: String,
    types: List[(c.universe.TermName, List[(Any, String, Option[String])])]) = {
    <typeSystemDescription xmlns="http://uima.apache.org/resourceSpecifier">
      <name>{ name }</name>
      <types>
      { types.map { case (name, features) =>
          <typeDescription>
            <name>{ baseName + "." + name }</name>
            <description></description>
            <supertypeName>{ "uima.tcas.Annotation" }</supertypeName>
            <features>
            {
              for ((name, typ, collType) ← features) yield {
                <featureDescription>
                  <name>{ name }</name>
                  <rangeTypeName>{ typToUimaType(typ, collType) }</rangeTypeName>
                  { collType.map { x =>
                    <multipleReferencesAllowed>{ typToUimaType(typ, None) }</multipleReferencesAllowed>
                  }.toSeq}
                </featureDescription>
              }
            }
            </features>
          </typeDescription>
      }}
      </types>
    </typeSystemDescription>
  }

  def typToUimaType(typeName: String, collType: Option[String]): String = {
    (typeName, collType) match {
      case ("Int", None) => "uima.cas.Integer"
      case ("Float", None) => "uima.cas.Float"
      case ("String", None) => "uima.cas.String"
      case ("Byte", None) => "uima.cas.Byte"
      case ("Short", None) => "uima.cas.Short"
      case ("Long", None) => "uima.cas.Long"
      case ("Double", None) => "uima.cas.Double"
      case ("Boolean", None) => "uima.cas.Boolean"

      case ("Int", Some("Array")) => "uima.cas.IntegerArray"
      case ("Float", Some("Array")) => "uima.cas.FloatArray"
      case ("String", Some("Array")) => "uima.cas.StringArray"
      case ("Byte", Some("Array")) => "uima.cas.ByteArray"
      case ("Short", Some("Array")) => "uima.cas.ShortArray"
      case ("Long", Some("Array")) => "uima.cas.LongArray"
      case ("Double", Some("Array")) => "uima.cas.DoubleArray"
      case ("Boolean", Some("Array")) => "uima.cas.BooleanArray"

      // TODO: Add other supported types

      case _ => throw new Exception("this type is not supported by UIMA: " +
          collType.map(x => x + "[" + typeName + "]").getOrElse(typeName)
      )
    }
  }
}
