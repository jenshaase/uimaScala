package jenshaase.uimaScala.core.configuration

import org.specs2.Specification
import jenshaase.uimaScala.core.configuration._
import parameter._

class ConfigurationInitalizationSpec extends Specification { def is =
  
  "This is a specification to check the configuration system"  ^
  															   p^
  "ConfigMock should"										   ^
  	"have 4 parameters"										   ! nbParams(4)^
    "have a parameter called 'stringParam'"					   ! hasParam("stringParam")^
    "have a parameter called 'optStringParam'"				   ! hasParam("optStringParam")^
    "have a parameter called 'intParam'"				   	   ! hasParam("intParam")^
    "have a parameter called 'optIntParam'"					   ! hasParam("optIntParam")^
    "generate a key value list of parameters"				   ! createKeyValues^
    "load parameters from a UIMA Context"					   ! todo
    														   end
  
  def hasParam(name: String) =
    new ConfigMock().parameters.map(_.name).contains(name) must beTrue
    
  def nbParams(count: Int) =
    new ConfigMock().parameters.size must be equalTo(count)

  def createKeyValues = {
    val config = new ConfigMock().
    	stringParam("Test").intParam(100).optIntParam(200)
    	
    config.parameterKeyValues.toList.
    	sliding(2,2).map(l => Pair(l(0), l(1))).toList.sortBy(_._1) must be equalTo(List(
			("intParam", "100"),
			("optIntParam", "200"),
			("stringParam", "Test")))
  }
}

class ConfigMock extends Configurable with ConfigurationInitialization {
  object stringParam extends StringParameter(this)
  object optStringParam extends StringParameter(this) {
    override def defaultValue = "DEFAULT"
  }
    
  object intParam extends IntParameter(this)
  object optIntParam extends OptionalIntParameter(this)
    
  object somethingElse {
    def someMethod = "Anything"
  }
}