/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration.parameter

import jenshaase.uimaScala.core.configuration._

trait IntTypedParameter extends TypedParameter[Int] {

  def setFromAny(in: Any): Either[Failure, Option[Int]] = in match {
    case (n: Number)      ⇒ setOption(Some(n.intValue))
    case Some(n: Number)  ⇒ setOption(Some(n.intValue))
    case (n: Number) :: _ ⇒ setOption(Some(n.intValue))
    case _                ⇒ genericSetFromAny(in)
  }

  def setFromString(s: String): Either[Failure, Option[Int]] = s match {
    case "" if !mandatory_? ⇒ setOption(None)
    case _ ⇒ parse(s) match {
      case Right(i: Int)    ⇒ setOption(Some(i))
      case Left(f: Failure) ⇒ Left(f)
    }
  }

  def asObject = this.valueOption match {
    case Some(i: Int) ⇒ i.asInstanceOf[Object]
    case None         ⇒ null
  }

  protected def parse(s: String): Either[Failure, Int] = try {
    Right(s.toInt)
  } catch {
    case e: Exception ⇒ Left(Failure("Error: " + e.getMessage, Some(e)))
  }

  override def uimaType = "Integer"
}

class IntParameter[OwnerType <: Configurable](comp: OwnerType)
  extends Parameter[Int, OwnerType] with MandatoryTypedParameter[Int] with IntTypedParameter {

  def this(comp: OwnerType, value: Int) = {
    this(comp)
    set(value)
  }

  def defaultValue = 0

  def owner = comp
}

class OptionalIntParameter[OwnerType <: Configurable](comp: OwnerType)
  extends Parameter[Int, OwnerType] with OptionalTypedParameter[Int] with IntTypedParameter {

  def this(comp: OwnerType, value: Option[Int]) = {
    this(comp)
    setOption(value)
  }

  def owner = comp
}