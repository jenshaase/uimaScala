package jenshaase.uimaScala.core.resource

import jenshaase.uimaScala.core.configuration.Configurable
import org.apache.uima.resource.ExternalResourceDescription
import org.uimafit.factory.ExternalResourceFactory
import org.apache.uima.resource.SharedResourceObject
import java.net.URL
import java.io.File
import org.apache.uima.resource.ResourceSpecifier

trait BaseResource {

  private var resourceKey: String = _
  
  def name: String = resourceKey
  
  def mandatory_? = true
  
  private[resource] final def setName_!(newName: String): String = {
    resourceKey = newName
    resourceKey
  }
}

case class Failure(msg: String, exception: Option[Exception] = None)

trait TypedResourceWrapper[ThisType] extends BaseResource {
  type ParamsType
  
  private var boundResource: Option[ThisType] = None
  private[resource] var params: Map[ParamsType, ParamsType] = _
  private[resource] var clazz: Class[ThisType] = _
  
  def bindResourceFromAny(a: Any): Either[Failure, Option[ThisType]] = {
    a match {
      case x: ThisType => Right(this.bindResourceOption(Some(x)))
      case Some(x: ThisType) => Right(this.bindResourceOption(Some(x)))
      case _ => Left(Failure("Can not cast any to correct type"))
    }
  }
  
  def bindResourceOption(in: Option[ThisType]): Option[ThisType] =  {
    boundResource = in
    boundResource
  }
  
  def resourceOption: Option[ThisType] = boundResource
  
  def parameter = params;
  
  def parameterList: Seq[ParamsType] = parameter.toSeq.flatMap(p => List(p._1, p._2))
  
  def className = clazz;
  
  def createBinding(aed: ResourceSpecifier)
}

trait MandatoryTypedResource[ThisType] extends TypedResourceWrapper[ThisType] {
  override def mandatory_? = true
  
  def bindResource(in: ThisType): ThisType = {
    bindResourceOption(Some(in))
    in
  }
  
  def resource: ThisType = resourceOption get
  
  def get = resource
  
  def is = resource
}

trait OptionalTypedResourceWrapper[ThisType] extends TypedResourceWrapper[ThisType] {
  override def mandatory_? = false
  
  def bindResource(in: Option[ThisType]): Option[ThisType] = bindResourceOption(in)
  
  def resource: Option[ThisType] = resourceOption
  
  def get = resource
  
  def is = resource
}

trait OwnedResource[OwnerType <: Configurable] extends BaseResource {
  def owner: OwnerType
}

trait ResourceWrapper[T, OwnerType <: Configurable]
		extends OwnedResource[OwnerType] with TypedResourceWrapper[T] {}

trait TypedSharedResource[T <: SharedResourceObject] extends TypedResourceWrapper[T] {
  type ParamsType = Object
  
  private var dataUrl: String = _
  
  def bind(newClazz: Class[T], newUrl: String, newParams: Map[ParamsType, ParamsType]) {
    clazz = newClazz
    dataUrl = newUrl
    params = newParams
  }
  
  def url = dataUrl
  
  def bind(newClazz: Class[T], newUrl: URL, newParams: Map[ParamsType, ParamsType]) {
    bind(newClazz, newUrl.toString(), newParams)
  }
  
  def bind(newClazz: Class[T], newFile: java.io.File, newParams: Map[ParamsType, ParamsType]) {
    bind(newClazz, newFile.toURI().toURL(), newParams)
  }
  
  def createBinding(aed: ResourceSpecifier) = {
    ExternalResourceFactory.bindResource(
        aed,
        name,
        className,
        url,
        parameterList:_*)
  }
  
}

class SharedResource[T <: SharedResourceObject, OwnerType <: Configurable](comp: OwnerType)
	extends ResourceWrapper[T, OwnerType]
			with TypedSharedResource[T] with MandatoryTypedResource[T] {
  
  def apply(newClazz: Class[T], newUrl: String, newParams: Map[ParamsType, ParamsType] = Map.empty): OwnerType = {
    bind(newClazz, newUrl, newParams)
    owner
  }
  
  def apply(newClazz: Class[T], newUrl: String): OwnerType =
    apply(newClazz, newUrl.toString(), Map.empty[ParamsType, ParamsType])
  
  def apply(newClazz: Class[T], newUrl: URL): OwnerType =
    apply(newClazz, newUrl.toString(), Map.empty[ParamsType, ParamsType])
  
  def apply(newClazz: Class[T], newUrl: URL, newParams: Map[ParamsType, ParamsType]): OwnerType =
    apply(newClazz, newUrl.toString(), newParams)
  
  def apply(newClazz: Class[T], newFile: File): OwnerType =
	apply(newClazz, newFile, Map.empty[ParamsType, ParamsType])
  
  def apply(newClazz: Class[T], newFile: File, newParams: Map[ParamsType, ParamsType]): OwnerType =
    apply(newClazz, newFile.toURI().toURL(), newParams)
  
  def owner = comp
}

class OptionalSharedResource[T <: SharedResourceObject, OwnerType <: Configurable](comp: OwnerType)
	extends ResourceWrapper[T, OwnerType]
			with TypedSharedResource[T] with OptionalTypedResourceWrapper[T] {
  
  def apply(newClazz: Class[T], newUrl: String, newParams: Map[ParamsType, ParamsType]): OwnerType = {
    bind(newClazz, newUrl, newParams)
    owner
  }
  
  def apply(newClazz: Class[T], newUrl: String): OwnerType =
    apply(newClazz, newUrl.toString())
  
  def apply(newClazz: Class[T], newUrl: URL): OwnerType =
    apply(newClazz, newUrl.toString(), Map.empty[ParamsType, ParamsType])
  
  def apply(newClazz: Class[T], newUrl: URL, newParams: Map[ParamsType, ParamsType]): OwnerType =
    apply(newClazz, newUrl.toString(), newParams)
  
  def apply(newClazz: Class[T], newFile: File): OwnerType =
	apply(newClazz, newFile, Map.empty[ParamsType, ParamsType])
  
  def apply(newClazz: Class[T], newFile: File, newParams: Map[ParamsType, ParamsType]): OwnerType =
    apply(newClazz, newFile.toURI().toURL(), newParams)
  
  def owner = comp
}

trait TypedResource[T <: org.apache.uima.resource.Resource] extends TypedResourceWrapper[T] {
  
  type ParamsType = String
  
  def bind(newClazz: Class[T], newParams: Map[ParamsType, ParamsType] = Map.empty) = {
    clazz = newClazz
    params = newParams
  }
  
  def createBinding(aed: ResourceSpecifier) = {
    ExternalResourceFactory.bindResource(
        aed,
        name,
        className,
        parameterList:_*)
  }
}

class Resource[T <: org.apache.uima.resource.Resource, OwnerType <: Configurable](comp: OwnerType)
	extends ResourceWrapper[T, OwnerType]
			with TypedResource[T] with MandatoryTypedResource[T] {
  
  def apply(newClazz: Class[T], newParams: Map[ParamsType, ParamsType] = Map.empty): OwnerType = {
    bind(newClazz, newParams)
    owner
  }
  
  def owner = comp
}

class OptionalResource[T <: org.apache.uima.resource.Resource, OwnerType <: Configurable](comp: OwnerType)
	extends ResourceWrapper[T, OwnerType]
			with TypedResource[T] with OptionalTypedResourceWrapper[T] {
  
  def apply(newClazz: Class[T], newParams: Map[ParamsType, ParamsType] = Map.empty): OwnerType = {
    bind(newClazz, newParams)
    owner
  }
  
  def owner = comp
}