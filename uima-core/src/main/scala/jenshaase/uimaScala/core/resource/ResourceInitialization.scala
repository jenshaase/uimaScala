package jenshaase.uimaScala.core.resource

import jenshaase.uimaScala.core.configuration.Configurable
import java.lang.reflect.Method
import scala.collection.mutable.ListBuffer
import org.apache.uima.UimaContext
import org.apache.uima.resource.ResourceInitializationException
import org.apache.uima.resource.ResourceAccessException
import org.uimafit.descriptor.ExternalResourceLocator

trait ResourceInitialization { this: Configurable =>

  private var resourceList: List[ResourceHolder] = Nil
  
  private val resTempArray: ListBuffer[ResourceHolder] = new ListBuffer[ResourceHolder]
  val resMethods = this.getClass.getMethods
  introspectResources(this, resMethods) {
    case (v, mf) => {
      resTempArray += ResourceHolder(mf.name, v, mf)
    }
  }
  resourceList = resTempArray.toList
  
  protected def introspectResources(comp: Configurable, methods: Array[Method])(f: (Method, ResourceWrapper[_, Configurable]) => Any): Unit = {
    val potentialResources = methods.toList.filter(isResource)
    
    val map: Map[String, List[Method]] = potentialResources.foldLeft[Map[String, List[Method]]](Map()) {
      case (map, method) =>
        val name = method.getName
        map + (name -> (method :: map.getOrElse(name, Nil)))
    }
    
    val realMeth = map.values.map(_.sortWith {
      case (a, b) => !a.getReturnType().isAssignableFrom(b.getReturnType)
    }).map(_.head)
    
    for (v <- realMeth) {
      v.invoke(comp) match {
        case mf: ResourceWrapper[_, Configurable] =>
          	mf.setName_!(v.getName)
          	f(v, mf)
        case _ =>
      }
    }
  }
  
  def resources = resourceList.map(_.resource(this))
  
  def loadResources(context: UimaContext) = {
    resources.foreach { r =>
      var value: Object = null;
      try {
    	  value = context.getResourceObject(r.name)
      } catch {
        case e: Exception=> throw new ResourceInitializationException(e)
      }
      
      if (value.isInstanceOf[ExternalResourceLocator]) {
        value = value.asInstanceOf[ExternalResourceLocator].getResource()
      }
      
      if (r.mandatory_? && value == null) {
        throw new ResourceInitializationException(new IllegalStateException("Mandatory resource '%s' is not set".format(r.name)))
      }
      
      if (value != null) {
        r.bindResourceFromAny(value) match {
          case Left(f: Failure) => throw f.exception.map(new ResourceInitializationException(_)).getOrElse(new ResourceInitializationException())
          case _ =>
        }
      }
    }
  }
  
  def isResource(m: Method) =
    !m.isSynthetic && classOf[ResourceWrapper[_, _]].isAssignableFrom(m.getReturnType)
  
  case class ResourceHolder(name: String, method: Method, metaParameter: ResourceWrapper[_, Configurable]) {
    def resource(inst: Configurable): ResourceWrapper[_, _] = method.invoke(inst).asInstanceOf[ResourceWrapper[_, Configurable]]
  }
}