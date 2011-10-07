/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration

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

  import jenshaase.uimaScala.core.CastFactory._

  private var data: Option[ThisType] = None

  /**
   * Sets a new value to this parameter
   */
  def :=(in: ThisType) =
    data = Some(in)

  /**
   * Set the parameter value by an object
   */
  def setFromUimaType(in: Any) = fromUima[ThisType](in) match {
    case Some(d: ThisType) ⇒ :=(d)
    case None              ⇒ // Leave to non
  }

  /**
   * Coverts this parameter value to a uima type
   */
  def toUimaType: Object = toUima(value) match {
    case Some(s) ⇒ s.asInstanceOf[Object]
    case None    ⇒ null
  }

  /**
   * Checks if the parameter is mutlivalued
   */
  override def multiValued_? = mf match {
    case l: Manifest[List[_]]  ⇒ true
    case s: Manifest[Seq[_]]   ⇒ true
    case a: Manifest[Array[_]] ⇒ true
    case _                     ⇒ false
  }

  def value: ThisType = data getOrElse defaultValue

  def is = value

  def get = value

  def uimaType = if (multiValued_?) _uimaType(mf.typeArguments.head) else _uimaType(mf)

  def _uimaType(m: Manifest[_]) = m match {
    case a: Manifest[Int]     ⇒ "Integer"
    case a: Manifest[Float]   ⇒ "Float"
    case a: Manifest[Boolean] ⇒ "Boolean"
    case _                    ⇒ "String"
  }
}