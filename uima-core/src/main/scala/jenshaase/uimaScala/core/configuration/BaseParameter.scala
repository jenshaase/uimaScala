/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration
import org.apache.uima.analysis_component.AnalysisComponent

trait Configurable {}

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
trait TypedParameter[ThisType] extends BaseParameter {
  // The value type. Set later
  type ValueType

  /**
   * Holds the data of the parameter
   */
  private var data: Option[ThisType] = None

  /**
   * Set the parameter value by string
   */
  def setFromString(s: String): Either[Failure, Option[ThisType]]

  /**
   * Set the parameter value by an object
   */
  def setFromAny(a: Any): Either[Failure, Option[ThisType]]

  /**
   * Converts the value to string
   */
  def asString: String

  /**
   * Helper method often used in @see #setFromAny(a: Any)
   */
  protected final def genericSetFromAny(in: Any)(implicit m: Manifest[ThisType]): Either[Failure, Option[ThisType]] = in match {
    case value if m.erasure.isInstance(value)        ⇒ setOption(Some(value.asInstanceOf[ThisType]))
    case Some(value) if m.erasure.isInstance(value)  ⇒ setOption(Some(value.asInstanceOf[ThisType]))
    case (value) :: _ if m.erasure.isInstance(value) ⇒ setOption(Some(value.asInstanceOf[ThisType]))
    case (value: String)                             ⇒ setFromString(value)
    case Some(value: String)                         ⇒ setFromString(value)
    case (value: String) :: _                        ⇒ setFromString(value)
    case null | None                                 ⇒ setOption(defaultValueOption)
    case Some(other)                                 ⇒ setFromString(String.valueOf(other))
    case other                                       ⇒ setFromString(String.valueOf(other))
  }

  /**
   * Set the parameter value with an Option
   */
  def setOption(in: Option[ThisType]): Either[Failure, Option[ThisType]] = in match {
    case Some(_) ⇒ {
      data = in
      set_?(true)
      Right(data)
    }
    case _ if !mandatory_? ⇒ {
      data = in
      set_?(true)
      Right(data)
    }
    case _ ⇒ Left(Failure("Value required for " + name))
  }

  /**
   * The default value
   */
  def defaultValue: ValueType

  protected def defaultValueOption: Option[ThisType]

  /**
   * Returns the current value
   */
  def valueOption: Option[ThisType] = data

}

/**
 * A owned parameter. This saves a reference to the Analysis Component.
 */
trait OwnedParameter[OwnerType <: Configurable] extends BaseParameter {
  def owner: OwnerType
}

/**
 * A mandatory typed parameter.
 *
 * We assume that this parameter is always set. So not Option wrapping
 * ist necessary
 */
trait MandatoryTypedParameter[ThisType] extends TypedParameter[ThisType] {
  // ValueType is ThisType
  type ValueType = ThisType

  override def mandatory_? = true

  /**
   * Set the parameter value
   */
  def set(in: ThisType): ThisType = setOption(Some(in)) fold (r ⇒ defaultValue,
    l ⇒ l getOrElse defaultValue)

  /**
   * Returns the value of this parameter. If not set defaultValue will
   * be returned
   */
  def value: ThisType = valueOption getOrElse defaultValue

  /**
   * @see #value
   */
  def get: ThisType = value

  /**
   * @see #value
   */
  def is: ThisType = value

  protected def defaultValueOption = if (!mandatory_?) None else Some(defaultValue)
}

/**
 * Optional typed parameter
 *
 * Most method are wrapped in a Option
 */
trait OptionalTypedParameter[ThisType] extends TypedParameter[ThisType] {
  // Set the value type
  type ValueType = Option[ThisType]

  override def mandatory_? = false

  /**
   * Set the value to this parameter.
   * If None given the defaultValue is set
   */
  def set(in: Option[ThisType]): Option[ThisType] = setOption(in) fold (r ⇒ defaultValue,
    l ⇒ l orElse defaultValue)

  /**
   * Returns the value of the parameter
   */
  def value: Option[ThisType] = valueOption orElse defaultValue

  /**
   * @see #value
   */
  def get: Option[ThisType] = value

  /**
   * @see #value
   */
  def is: Option[ThisType] = value

  /**
   * Returns the default value
   */
  def defaultValue: ValueType = None

  protected def defaultValueOption = defaultValue
}

/**
 * The parameter trait.
 *
 * Use this for the real implementations
 */
trait Parameter[ThisType, OwnerType <: Configurable] extends OwnedParameter[OwnerType] with TypedParameter[ThisType] {

  /**
   * @see #apply(in: Option[ThisType])
   */
  def apply(in: ThisType): OwnerType = apply(Some(in))

  /**
   * Sets the value and returns the owner type to set new values
   */
  def apply(in: Option[ThisType]): OwnerType = {
    this.setOption(in)
    owner
  }
}