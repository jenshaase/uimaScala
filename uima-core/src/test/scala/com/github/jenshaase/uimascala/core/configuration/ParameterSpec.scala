/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.configuration

import org.specs2.Specification
import com.github.jenshaase.uimascala.core.configuration._
import java.util.regex.Pattern

class ParameterSpec extends Specification {

  // format: OFF
  def is = s2"""
    A Parameter can
      return default values if not value is set ${defaultVal}
      have a new value ${newVal}
      be set from uima ${fromUima}
      be converted to uima ${toUima}
      be mutli valued ${multiVal}
      be single valued ${singleVal}
      have a correct uima type ${uimaType}
  """

  def defaultVal = {
    object param extends Parameter[String]("a")
    param.is must_== "a"
  }

  def newVal = {
    object param extends Parameter[String]("a")
    param := "b"
    param.is must_== "b"
  }

  def fromUima = {
    object param extends Parameter[Pattern](Pattern.compile("[A-Z]"))
    param.setFromUimaType("[1-4]")
    param.is.pattern must_== "[1-4]"
  }

  def toUima = {
    object param extends Parameter[Pattern](Pattern.compile("[A-Z]"))
    param.toUimaType must beRight
    param.toUimaType.right.get must_== "[A-Z]"
  }

  def multiVal = {
    object l extends Parameter[List[String]](List("b"))
    object s extends Parameter[Seq[String]](Seq("b"))
    object a extends Parameter[Array[String]](Array("b"))

    l.multiValued_? must_== true
    s.multiValued_? must_== true
    a.multiValued_? must_== true
  }

  def singleVal = {
    object param extends Parameter[String]("b")
    param.multiValued_? must_== false
  }

  def uimaType = {
    object p1 extends Parameter[Pattern](Pattern.compile("[A-Z]"))
    object p2 extends Parameter[String]("a")
    object p3 extends Parameter[Float](1.2f)
    object p4 extends Parameter[Boolean](true)
    object p5 extends Parameter[Int](2)

    object p6 extends Parameter[List[Pattern]](List(Pattern.compile("[A-Z]")))
    object p7 extends Parameter[List[String]](List("a"))
    object p8 extends Parameter[List[Float]](List(1.2f))
    object p9 extends Parameter[List[Boolean]](List(true))
    object p10 extends Parameter[List[Int]](List(2))

    p1.uimaType must_== "String"
    p2.uimaType must_== "String"
    p3.uimaType must_== "Float"
    p4.uimaType must_== "Boolean"
    p5.uimaType must_== "Integer"

    p6.uimaType must_== "String"
    p7.uimaType must_== "String"
    p8.uimaType must_== "Float"
    p9.uimaType must_== "Boolean"
    p10.uimaType must_== "Integer"
  }
}
