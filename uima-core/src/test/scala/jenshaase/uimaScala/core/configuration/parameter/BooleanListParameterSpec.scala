/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration.parameter

import org.specs2.Specification
import jenshaase.uimaScala.core.configuration._
import parameter._

class BooleanListParameterSpec extends Specification {

  def is =
    "This specification describes the boolean list parameter" ^
      p ^
      "The boolean list parameter can" ^
      "be set by list" ! setList ^
      "be set by array" ! setArray ^
      "be converted to object" ! convertObject ^
      "return an convert error" ! convertError ^
      end ^
      "The optinal boolean parameter should" ^
      "have a default value" ! default ^
      end

  def setList = {
    val m = new BooleanListParamMock()

    m.boolListParam.setFromAny(List(true, false)) must beRight
    m.boolListParam.is must be equalTo (List(true, false))
  }

  def setArray = {
    val m = new BooleanListParamMock()

    m.boolListParam.setFromAny(Array(true, false)) must beRight
    m.boolListParam.is must be equalTo (List(true, false))
  }

  def convertObject = {
    val m = new BooleanListParamMock().boolListParam(List(true, false))

    m.boolListParam.asObject must be equalTo (Array(true, false).asInstanceOf[Object])
  }

  def convertError = {
    val m = new BooleanListParamMock()
    m.boolListParam.setFromString("something") must beLeft
  }

  def default = {
    val m = new BooleanListParamMock()

    m.optBoolListParam.is must be equalTo (Some(List(true)))
  }

}

class BooleanListParamMock extends Configurable {
  object boolListParam extends BooleanListParameter(this)

  object optBoolListParam extends OptionalBooleanListParameter(this, Some(List(true)))
}