/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration.parameter

import jenshaase.uimaScala.core.configuration._

trait StringTypedParameter extends TypedParameter[String] {

  def setFromAny(in: Any): Either[Failure, Option[String]] = in match {
    case seq: Seq[_] if !seq.isEmpty ⇒ setFromAny(seq.head)
    case _                           ⇒ genericSetFromAny(in)
  }

  def setFromString(s: String): Either[Failure, Option[String]] = s match {
    case "" if !mandatory_? ⇒ setOption(None)
    case _                  ⇒ setOption(Some(s))
  }

  def asObject = this.valueOption match {
    case Some(s: String) ⇒ s.asInstanceOf[Object]
    case None            ⇒ null
  }
}

class StringParameter[OwnerType <: Configurable](comp: OwnerType)
  extends Parameter[String, OwnerType] with MandatoryTypedParameter[String] with StringTypedParameter {

  def this(comp: OwnerType, value: String) = {
    this(comp)
    set(value)
  }

  def defaultValue = ""

  def owner = comp
}

class OptionalStringParameter[OwnerType <: Configurable](comp: OwnerType)
  extends Parameter[String, OwnerType] with OptionalTypedParameter[String] with StringTypedParameter {

  def this(comp: OwnerType, value: Option[String]) = {
    this(comp)
    setOption(value)
  }

  def owner = comp
}