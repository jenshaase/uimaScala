/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.configuration

import org.apache.uima.analysis_component.AnalysisComponent

trait Configurable {}

class ConfigurationBuilder[T <: Configurable](conf: T) {
  def config(mutators: T ⇒ Unit*) = {
    for (f ← mutators) f(conf)
    conf
  }
}

/**
 * Base Parameter trait
 */
trait BaseParameter {

  // The parameter name
  private var fieldName: String = _
  private var set = false

  protected def set_?(b: Boolean) = set = b

  def set_? : Boolean = set

  /**
   * Returns the parameter name
   */
  def name: String = fieldName

  /**
   * Returns the parameter description
   * Default: None
   */
  def description: Option[String] = None

  /**
   * Is this parameter mandatory?
   */
  def mandatory_? = true

  /**
   * If the parameter can take multiple values (collections)
   */
  def multiValued_? = false

  /**
   * Default is string.
   * Also possible: Integer, Float, Boolean
   */
  def uimaType: String

  /**
   * Sets the parameter name
   */
  private[configuration] final def setName_!(newName: String): String = {
    fieldName = newName
    fieldName
  }
}

case class Failure(msg: String, exception: Option[Exception] = None)

/**
 * A typed parameter
 */
abstract class Parameter[ThisType](val defaultValue: ThisType)(implicit mf: Manifest[ThisType])
    extends BaseParameter {

  import com.github.jenshaase.uimascala.core.CastFactory._

  private var data: Option[ThisType] = None

  /**
   * Sets a new value to this parameter
   */
  def :=(in: ThisType) =
    data = Some(in)

  /**
   * Set the parameter value by an object
   */
  def setFromUimaType(in: Any): Either[Failure, ThisType] = fromUima[ThisType](in) match {
    case Right(Some(d: ThisType)) ⇒ { :=(d); Right(d) }
    case Right(None)              ⇒ Left(Failure("Value could not be casted: " + in.toString))
    case Left(l)                  ⇒ Left(l)
  }

  /**
   * Coverts this parameter value to a uima type
   */
  def toUimaType: Either[Failure, Object] = toUima(value) match {
    case Right(Some(s)) ⇒ Right(s.asInstanceOf[Object])
    case Right(None)    ⇒ Left(Failure("Value could not be casted: " + value))
    case Left(l)        ⇒ Left(l)
  }

  /**
   * Checks if the parameter is mutlivalued
   */
  override def multiValued_? = mf.erasure.toString match {
    case "class scala.collection.immutable.List" ⇒ true
    case "interface scala.collection.Seq"        ⇒ true
    case s: String if (s.startsWith("class [L")) ⇒ true
    case _                                       ⇒ false
  }

  def value: ThisType = data getOrElse defaultValue

  def is = value

  def get = value

  def uimaType =
    if (multiValued_?)
      _uimaType(mf.typeArguments.head.erasure.toString)
    else
      _uimaType(mf.erasure.toString)

  def _uimaType(s: String) = s match {
    case "int" | "class java.lang.Integer" ⇒ "Integer"
    case "float"                           ⇒ "Float"
    case "boolean"                         ⇒ "Boolean"
    case _                                 ⇒ "String"
  }
}