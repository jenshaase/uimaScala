/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration.parameter

import org.specs2.Specification
import jenshaase.uimaScala.core.configuration._
import parameter._

class IntParameterSpec extends Specification { def is =
  
  "This specification describes the integer parameter"		^
  															p^
  "The integer parameter can"								^
  	"be set by string"									    ! setString^
  	"be set by integer"										! setInt^
  	"be converted to string"								! convertString^
  	"return an convert error"								! convertError^
  															end^
  "The optinal integer parameter should"					^
    "have a default value"									! default^
    														end
    														
  def setString = {
	val m = new IntParamMock()

	m.intParam.setFromString("1") must beRight
	m.intParam.is must be equalTo(1)
  }

  def setInt = {
    val m = new IntParamMock()
    
    m.intParam.setFromAny(1) must beRight
    m.intParam.is must be equalTo(1)
  }
  
  def convertString = {
    val m = new IntParamMock().intParam(1)
    
    m.intParam.asString must be equalTo("1")
  }
  
  def convertError = {
    val m = new IntParamMock()
    m.intParam.setFromString("test") must beLeft
  }
  
  def default = {
    val m = new IntParamMock()
    
    m.optIntParam.is must be equalTo(Some(100))
  }
  

}

class IntParamMock extends Configurable {
  object intParam extends IntParameter(this)
  
  object optIntParam extends OptionalIntParameter(this) {
    override def defaultValue = Some(100)
  }
}