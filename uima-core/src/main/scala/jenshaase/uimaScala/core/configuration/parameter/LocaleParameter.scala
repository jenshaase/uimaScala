package jenshaase.uimaScala.core.configuration.parameter

import java.util.Locale
import jenshaase.uimaScala.core.configuration._
import org.apache.uima.analysis_component.AnalysisComponent

trait LocaleTypedParameter extends TypedParameter[Locale]{

  def setFromAny(in: Any): Either[Failure, Option[Locale]] = in match {
    case (l: Locale) => setOption(Some(l))
    case Some(l: Locale) => setOption(Some(l))
    case (l: Locale) :: _ => setOption(Some(l))
    case _ => genericSetFromAny(in)
  }
  
  def setFromString(s: String): Either[Failure, Option[Locale]] = s match {
    case "" if !mandatory_? => setOption(None)
    case _ => setOption(Some(new Locale(s)))
  }
  
  def asString = this.valueOption match {
    case Some(l: Locale) => l.getLanguage
    case None => ""
  }
}

class LocaleParameter[OwnerType <: Configurable](comp: OwnerType)
	extends Parameter[Locale, OwnerType] with MandatoryTypedParameter[Locale] with LocaleTypedParameter {
  
  def this(comp: OwnerType, value: Locale) = {
    this(comp)
    set(value)
  }
  
  def this(comp: OwnerType, value: String) = {
    this(comp)
    set(new Locale(value))
  }
  
  def defaultValue = Locale.getDefault
  
  def owner = comp
}

class OptionalLocaleParameter[OwnerType <: Configurable](comp: OwnerType)
	extends Parameter[Locale, OwnerType] with OptionalTypedParameter[Locale] with LocaleTypedParameter {
  
  def this(comp: OwnerType, value: Option[Locale]) = {
    this(comp)
    setOption(value)
  }
  
  def owner = comp
}