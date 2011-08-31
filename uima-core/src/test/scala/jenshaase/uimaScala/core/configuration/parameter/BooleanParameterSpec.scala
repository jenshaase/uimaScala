package jenshaase.uimaScala.core.configuration.parameter

import org.specs2.Specification
import jenshaase.uimaScala.core.configuration._
import parameter._

class BooleanParameterSpec extends Specification { def is =
  
  "This specification describes the boolean parameter"		^
  															p^
  "The boolean parameter can"								^
  	"be set by string"									    ! setString^
  	"be set by boolean"										! setInt^
  	"be converted to string"								! convertString^
  	"return an convert error"								! convertError^
  															end^
  "The optinal boolean parameter should"					^
    "have a default value"									! default^
    														end
    														
  def setString = {
	val m = new BooleanParamMock()

	m.boolParam.setFromString("true") must beRight
	m.boolParam.is must be equalTo(true)
  }

  def setInt = {
    val m = new BooleanParamMock()
    
    m.boolParam.setFromAny(true) must beRight
    m.boolParam.is must be equalTo(true)
  }
  
  def convertString = {
    val m = new BooleanParamMock().boolParam(true)
    
    m.boolParam.asString must be equalTo("true")
  }
  
  def convertError = {
    val m = new BooleanParamMock()
    m.boolParam.setFromString("something") must beLeft
  }
  
  def default = {
    val m = new BooleanParamMock()
    
    m.optBoolParam.is must be equalTo(Some(true))
  }

}

class BooleanParamMock extends Configurable {
  object boolParam extends BooleanParameter(this)
  
  object optBoolParam extends OptionalBooleanParameter(this, Some(true))
}