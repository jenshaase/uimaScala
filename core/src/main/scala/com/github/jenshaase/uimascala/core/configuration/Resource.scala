/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.configuration

import java.net.URL
import java.io.File
import org.apache.uima.resource.ResourceSpecifier
import org.apache.uima.fit.factory.ExternalResourceFactory
import org.apache.uima.resource.SharedResourceObject

trait BaseResource {

  private var resourceKey: String = _

  def name: String = resourceKey

  def description: String = ""

  def interfaceName: String

  def mandatory_? = true

  private[configuration] final def setName_!(newName: String): String = {
    resourceKey = newName
    resourceKey
  }
}

trait TypedResource[ThisType, ParamType] extends BaseResource {

  private var boundResource: Option[ThisType] = None
  private[configuration] var parameters: Option[Map[ParamType, ParamType]] = None

  def params = parameters getOrElse defaultParameter

  def defaultParameter: Map[ParamType, ParamType]

  def parameterList: Seq[ParamType] =
    params.toSeq.flatMap(p ⇒ List(p._1, p._2))

  def setFromUima(a: Any) = a match {
    case x: ThisType       ⇒ Right(bind(x))
    case Some(x: ThisType) ⇒ Right(bind(x))
    case _                 ⇒ Left(Failure("Can not bind resource from uima context: " + name))
  }

  def bind(newResource: ThisType) = {
    boundResource = Some(newResource)
    boundResource
  }

  def resource = boundResource get

  def createBinding(aed: ResourceSpecifier)

  def className: Class[_ <: ThisType]

  def interfaceName = className.getName
}

case class SharedBinding[T](url: String, params: Map[Object, Object] = Map.empty)
object SharedBinding {

  def apply[T](url: URL) =
    new SharedBinding[T](url.toString, Map.empty)

  def apply[T](url: URL, params: Map[Object, Object]) =
    new SharedBinding[T](url.toString, params)

  def apply[T](url: File) =
    new SharedBinding[T](url.toURI().toURL().toString, Map.empty)

  def apply[T](url: File, params: Map[Object, Object]) =
    new SharedBinding[T](url.toURI().toURL().toString, params)
}

abstract class SharedResource[ThisType <: SharedResourceObject](
  val defaultURL: String,
  val defaultParams: Map[Object, Object] = Map.empty)(implicit m: Manifest[ThisType])
    extends TypedResource[ThisType, Object] {

  private var dataUrl: Option[String] = None
  private var clazz: Option[Class[_ <: ThisType]] = None

  def this(defaultUrl: URL, defaultParams: Map[Object, Object])(implicit m: Manifest[ThisType]) =
    this(defaultUrl.toString, defaultParams)

  def this(defaultUrl: File, defaultParams: Map[Object, Object])(implicit m: Manifest[ThisType]) =
    this(defaultUrl.toURI().toURL(), defaultParams)

  def :=[T <: ThisType](bind: SharedBinding[T])(implicit mf: Manifest[T]) = {
    clazz = Some(mf.erasure.asInstanceOf[Class[T]])
    dataUrl = Some(bind.url)
    parameters = Some(bind.params)
  }

  def url = dataUrl getOrElse defaultURL

  def defaultParameter = defaultParams

  def defaultClass = m.erasure.asInstanceOf[Class[ThisType]]

  def className: Class[_ <: ThisType] = clazz getOrElse defaultClass

  // format: OFF
  def createBinding(aed: ResourceSpecifier) = {
    ExternalResourceFactory.bindResource(
      aed,
      name,
      className,
      url,
      parameterList:_*)
  }
}

case class Binding[T](params: Map[String, String] = Map.empty)

abstract class Resource[ThisType <: org.apache.uima.resource.Resource](
  val defaultParams: Map[String, String] = Map.empty)(implicit m: Manifest[ThisType])
  extends TypedResource[ThisType, String] {

  private var clazz: Option[Class[_ <: ThisType]] = None

  def defaultClass = m.erasure.asInstanceOf[Class[ThisType]]

  def :=[T <: ThisType](bind: Binding[T])(implicit mf: Manifest[T]) = {
    clazz = Some(mf.erasure.asInstanceOf[Class[T]])
    parameters = Some(bind.params)
  }

  def defaultParameter = defaultParams

  def className: Class[_ <: ThisType] = clazz getOrElse defaultClass

  // format: OFF
  def createBinding(aed: ResourceSpecifier) = {
    ExternalResourceFactory.bindResource(
      aed,
      name,
      className,
      parameterList:_*)
  }
}
