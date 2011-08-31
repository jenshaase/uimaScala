package jenshaase.uimaScala.core.configuration.parameter

import org.specs2.Specification
import jenshaase.uimaScala.core.configuration._
import parameter._
import java.util.Locale

class LocaleParameterSpec extends Specification { def is =
  
  "This specification describes the locale parameter"		^
  															p^
  "The locale parameter can"								^
  	"be set by string"									    ! setString^
  	"be set by locale"										! setLocale^
  	"be converted to string"								! convertString^
  															end^
  "The optinal locale parameter should"						^
    "have a default value"									! default^
    														end

  def setString = {
	val m = new LocaleParamMock()

	m.localeParam.setFromString("de") must beRight
	m.localeParam.is.getLanguage must be equalTo("de")
  }

  def setLocale = {
    val m = new LocaleParamMock()
    
    m.localeParam.setFromAny(Locale.ENGLISH) must beRight
    m.localeParam.is.getLanguage must be equalTo("en")
  }
  
  def convertString = {
    val m = new LocaleParamMock().localeParam(Locale.ENGLISH)
    
    m.localeParam.asString must be equalTo("en")
  }
  
  def default = {
    val m = new LocaleParamMock()
    
    m.optLocaleParam.is.map(_.getLanguage) must be equalTo(Some("en"))
  }
}

class LocaleParamMock extends Configurable {
  object localeParam extends LocaleParameter(this)
  
  object optLocaleParam extends OptionalLocaleParameter(this) {
    override def defaultValue = Some(Locale.ENGLISH)
  }
}