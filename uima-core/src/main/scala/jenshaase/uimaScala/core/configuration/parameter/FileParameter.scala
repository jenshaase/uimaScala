package jenshaase.uimaScala.core.configuration.parameter

import jenshaase.uimaScala.core.configuration._
import java.io.File

/**
 * java.io.File parameter
 * 
 * creates a File from a given String
 */
trait FileTypedParameter extends TypedParameter[File] {

  def setFromAny(in: Any): Either[Failure, Option[File]] = in match {
    case (f: File) => setOption(Some(f))
    case Some(f: File) => setOption(Some(f))
    case _ => genericSetFromAny(in)
  }
  
  def setFromString(s: String): Either[Failure, Option[File]] = s match {
    case "" if !mandatory_? => setOption(None)
    case _ => setOption(Some(new File(s)))
  }
  
  def asString = this.valueOption match {
    case Some(f: File) => f.getAbsolutePath
    case None => ""
  }
}

/**
 * The file parameter class
 */
class FileParameter[OwnerType <: Configurable](comp: OwnerType)
	extends Parameter[File, OwnerType] with MandatoryTypedParameter[File] with FileTypedParameter {
  
  def this(comp: OwnerType, value: File) = {
    this(comp)
    set(value)
  }
  
  def this(comp: OwnerType, value: String) = {
    this(comp)
    set(new File(value))
  }
  
  def defaultValue = null
  
  def owner = comp
}

/**
 * The optional file parameter class
 */
class OptionalFileParameter[OwnerType <: Configurable](comp: OwnerType)
	extends Parameter[File, OwnerType] with OptionalTypedParameter[File] with FileTypedParameter {
  
  def this(comp: OwnerType, value: Option[File]) = {
    this(comp)
    setOption(value)
  }
  
  def owner = comp
}