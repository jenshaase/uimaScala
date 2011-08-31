package jenshaase.uimaScala.core.configuration.parameter

import jenshaase.uimaScala.core.configuration._
import java.util.regex.Pattern

trait PatternTypedParameter extends TypedParameter[Pattern] {

  def setFromAny(in: Any): Either[Failure, Option[Pattern]] = in match {
    case (p: Pattern) => setOption(Some(p))
    case Some(p: Pattern) => setOption(Some(p))
    case _ => genericSetFromAny(in)
  }
  
  def setFromString(s: String): Either[Failure, Option[Pattern]] = s match {
    case "" if !mandatory_? => setOption(None)
    case _ => compile(s) match {
      case Right(p: Option[Pattern]) => setOption(p)
      case Left(f: Failure) => Left(f)
    }
  }
  
  def asString = this.valueOption match {
    case Some(p: Pattern) => p.pattern
    case None => ""
  }
  
  protected def compile(s: String): Either[Failure, Option[Pattern]] = try {
    Right(Some(Pattern.compile(s)))
  } catch {
    case e: Exception => Left(Failure("Error: " + e.getMessage, Some(e)))
  }
}

class PatternParameter[OwnerType <: Configurable](comp: OwnerType)
	extends Parameter[Pattern, OwnerType] with MandatoryTypedParameter[Pattern] with PatternTypedParameter {
  
  def this(comp: OwnerType, value: Pattern) = {
    this(comp)
    set(value)
  }
  
  def this(comp: OwnerType, value: String) = {
    this(comp)
    set(Pattern.compile(value))
  }
  
  def defaultValue: Pattern = null
  
  def owner = comp
}

class OptionalPatternParameter[OwnerType <: Configurable](comp: OwnerType)
	extends Parameter[Pattern, OwnerType] with OptionalTypedParameter[Pattern] with PatternTypedParameter {
  
  def this(comp: OwnerType, value: Option[Pattern]) = {
    this(comp)
    setOption(value)
  }
  
  def owner = comp
}