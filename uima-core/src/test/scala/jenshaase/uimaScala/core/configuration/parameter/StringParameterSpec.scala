/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration.parameter

import org.specs2.Specification
import jenshaase.uimaScala.core.configuration._
import parameter._

class StringParameterSpec extends Specification {
  def is =

    "This specification describes the string parammeter" ^
      p ^
      "The string parameter can" ^
      "be set by any string" ! setString ^
      "be set by string" ! setAny ^
      "be converted to string" ! convertString ^
      end ^
      "The optinal integer parameter should" ^
      "have a default value" ! default ^
      end
  def setString = {
    val m = new StringParamMock()

    m.stringParam.setFromString("test") must beRight
    m.stringParam.is must be equalTo ("test")
  }

  def setAny = {
    val m = new StringParamMock()

    m.stringParam.setFromAny("test") must beRight
    m.stringParam.is must be equalTo ("test")
  }

  def convertString = {
    val m = new StringParamMock().stringParam("test")

    m.stringParam.asObject must be equalTo ("test".asInstanceOf[Object])
  }

  def default = {
    val m = new StringParamMock()

    m.optStringParam.is must be equalTo (Some("hello"))
  }
}

class StringParamMock extends Configurable {
  object stringParam extends StringParameter(this)

  object optStringParam extends OptionalStringParameter(this) {
    override def defaultValue = Some("hello")
  }
}