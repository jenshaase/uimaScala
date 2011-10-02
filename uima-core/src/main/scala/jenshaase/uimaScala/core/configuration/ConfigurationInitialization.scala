/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration

import scala.collection.mutable.ListBuffer
import org.apache.uima.analysis_component.AnalysisComponent
import java.lang.reflect.Method
import org.apache.uima.UimaContext
import org.apache.uima.resource.ResourceInitializationException

/**
 * Configuration Initalization trait
 *
 * This can be used whenever configuration parameters must be initalized
 */
trait ConfigurationInitialization { this: Configurable ⇒

  private var parameterList: List[ParameterHolder] = Nil

  private val tArray: ListBuffer[ParameterHolder] = new ListBuffer[ParameterHolder]
  val methods = this.getClass.getMethods
  introspect(this, methods) {
    case (v, mf) ⇒ tArray += ParameterHolder(mf.name, v, mf)
  }
  parameterList = tArray.toList

  /**
   * Uses reflection to find the parameters in the class
   */
  protected def introspect(comp: Configurable, methods: Array[Method])(f: (Method, Parameter[_, Configurable]) ⇒ Any): Unit = {
    val potentialParams = methods.toList.filter(isParameter)

    val map: Map[String, List[Method]] = potentialParams.foldLeft[Map[String, List[Method]]](Map()) {
      case (map, method) ⇒
        val name = method.getName
        map + (name -> (method :: map.getOrElse(name, Nil)))
    }

    val realMeth = map.values.map(_.sortWith {
      case (a, b) ⇒ !a.getReturnType().isAssignableFrom(b.getReturnType)
    }).map(_.head)

    for (v ← realMeth) {
      v.invoke(comp) match {
        case mf: Parameter[_, Configurable] ⇒
          mf.setName_!(v.getName)
          f(v, mf)
        case _ ⇒
      }
    }
  }

  /**
   * Returns all parameters for the class
   */
  def parameters = parameterList.map(_.parameter(this))

  /**
   * Uses the uima context to set the parameter
   */
  protected def loadParameter(context: UimaContext) = {
    parameters.foreach { f ⇒
      val value = context.getConfigParameterValue(f.name)

      if (f.mandatory_? && value == null) {
        throw new ResourceInitializationException(ResourceInitializationException.CONFIG_SETTING_ABSENT, Array(f.name))
      }

      if (value != null) {
        f.setFromAny(value) match {
          case Left(f: Failure) ⇒ throw f.exception.map(new ResourceInitializationException(_)).getOrElse(new ResourceInitializationException())
          case _                ⇒
        }
      }
    }
  }

  /**
   * Checks if a method is a subclass of Parameter
   */
  def isParameter(m: Method) =
    !m.isSynthetic && classOf[Parameter[_, _]].isAssignableFrom(m.getReturnType)

  class NiceObject[T <: AnyRef](x: T) {
    def niceClass: Class[_ <: T] = x.getClass.asInstanceOf[Class[T]]
  }
  implicit def toNiceObject[T <: AnyRef](x: T) = new NiceObject(x)

  def parameterKeyValues: Array[Object] = parameters.filter(_.set_?).flatMap { f ⇒
    Array(f.name.asInstanceOf[Object], f.asObject)
  }.toArray

  case class ParameterHolder(name: String, method: Method, metaParameter: Parameter[_, Configurable]) {
    def parameter(inst: Configurable): Parameter[_, Configurable] = method.invoke(inst).asInstanceOf[Parameter[_, Configurable]]
  }
}