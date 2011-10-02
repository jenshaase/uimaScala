/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration.parameter

import jenshaase.uimaScala.core.configuration._

trait BooleanListTypedParameter extends TypedParameter[List[Boolean]] {

  def setFromAny(in: Any): Either[Failure, Option[List[Boolean]]] = in match {
    case list: Array[Boolean]       ⇒ setOption(Some(list.toList))
    case Some(list: Array[Boolean]) ⇒ setOption(Some(list.toList))
    case list: List[Boolean]        ⇒ setOption(Some(list))
    case Some(list: List[Boolean])  ⇒ setOption(Some(list))
    case _                          ⇒ Left(Failure("Error parsing string into list"))
  }

  def setFromString(s: String): Either[Failure, Option[List[Boolean]]] =
    Left(Failure("Error parsing string into list"))

  def asObject = valueOption match {
    case Some(list: List[Boolean]) ⇒ list.toArray.asInstanceOf[Object]
    case None                      ⇒ null
  }

  override def uimaType = "Boolean"
}

class BooleanListParameter[OwnerType <: Configurable](comp: OwnerType)
  extends Parameter[List[Boolean], OwnerType] with MandatoryTypedParameter[List[Boolean]] with BooleanListTypedParameter {

  def this(comp: OwnerType, value: List[Boolean]) = {
    this(comp)
    set(value)
  }

  def defaultValue = Nil

  def owner = comp
}

class OptionalBooleanListParameter[OwnerType <: Configurable](comp: OwnerType)
  extends Parameter[List[Boolean], OwnerType] with OptionalTypedParameter[List[Boolean]] with BooleanListTypedParameter {

  def this(comp: OwnerType, value: Option[List[Boolean]]) = {
    this(comp)
    setOption(value)
  }

  def owner = comp
}