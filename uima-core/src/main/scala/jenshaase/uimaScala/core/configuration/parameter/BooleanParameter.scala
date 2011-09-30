/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration.parameter

import jenshaase.uimaScala.core.configuration._

trait BooleanTypedParameter extends TypedParameter[Boolean] {

  def setFromAny(in: Any): Either[Failure, Option[Boolean]] = in match {
    case (b: Boolean)      ⇒ setOption(Some(b))
    case Some(b: Boolean)  ⇒ setOption(Some(b))
    case (b: Boolean) :: _ ⇒ setOption(Some(b))
    case _                 ⇒ genericSetFromAny(in)
  }

  def setFromString(s: String): Either[Failure, Option[Boolean]] = s match {
    case "" if !mandatory_? ⇒ setOption(None)
    case _ ⇒ parse(s: String) match {
      case Right(b: Boolean) ⇒ setOption(Some(b))
      case Left(f: Failure)  ⇒ Left(f)
    }
  }

  def asString = this.valueOption match {
    case Some(b: Boolean) ⇒ b.toString
    case None             ⇒ ""
  }

  protected def parse(s: String): Either[Failure, Boolean] = try {
    val sl = s.toLowerCase
    if (sl.length == 0) Right(false)
    else {
      if (sl.charAt(0) == 't') Right(true)
      else if (sl.charAt(0) == 'f') Right(false)
      else if (sl == "yes") Right(true)
      else if (sl == "no") Right(false)
      else Right(s.toInt != 0)
    }
  } catch {
    case e: Exception ⇒ Left(Failure("Can not parse to boolean: " + e.getMessage, Some(e)))
  }
}

class BooleanParameter[OwnerType <: Configurable](comp: OwnerType)
  extends Parameter[Boolean, OwnerType] with MandatoryTypedParameter[Boolean] with BooleanTypedParameter {

  def this(comp: OwnerType, value: Boolean) = {
    this(comp)
    set(value)
  }

  def defaultValue = false

  def owner = comp
}

class OptionalBooleanParameter[OwnerType <: Configurable](comp: OwnerType)
  extends Parameter[Boolean, OwnerType] with OptionalTypedParameter[Boolean] with BooleanTypedParameter {

  def this(comp: OwnerType, value: Option[Boolean]) = {
    this(comp)
    setOption(value)
  }

  def owner = comp
}