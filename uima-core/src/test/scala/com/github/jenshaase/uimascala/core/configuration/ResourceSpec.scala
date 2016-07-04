/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.configuration

import java.util.regex.Pattern
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.resource.Resource_ImplBase
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource
import org.specs2.Specification

class ResourceSpec extends Specification {

  // format: OFF
  def is = s2"""
    A Resource should
      return default parameters ${defaultParams}
      convert parameter to a list ${paramToList}
      bind a resource ${bind}
      bind a resource from Uima ${bindUima}
      be set by a Binding ${setBinding}
      return a class name ${className}
      return a interface name ${interfaceName}
   
    A SharedResource should
      return default parameters ${sharedDefaultParams}
      convert parameter to a list ${sharedParamToList}
      bind a resource ${sharedBind}
      bind a resource from Uima ${sharedBindUima}
      be set by a Binding ${sharedSetBinding}
      return a class name ${sharedClassName}
      return a interface name ${sharedInterfaceName}
  """

  def defaultParams = {
    object r extends Resource[DummyRes](Map("a" -> "b"))
    r.params must_== Map("a" -> "b")
  }

  def paramToList = {
    object r extends Resource[DummyRes](Map("a" -> "b"))
    r.parameterList must_== Seq("a", "b")
  }

  def bind = {
    object r extends Resource[DummyRes]()
    val o = new DummyRes()
    r.bind(o)

    r.resource must_== o
  }

  def bindUima = {
    object r extends Resource[DummyRes]()
    val o = new DummyRes()
    r.setFromUima(o)

    r.resource must_== o
  }

  def setBinding = {
    object r extends Resource[DummyRes](Map("a" -> "b"))
    r := Binding(Map("c" -> "d"))
    r.params must_== Map("c" -> "d")
  }

  def className = {
    object r extends Resource[DummyRes](Map("a" -> "b"))
    r.className.getName must_== classOf[DummyRes].getName
  }

  def interfaceName = {
    object r extends Resource[DummyRes](Map("a" -> "b"))
    r.interfaceName must_== classOf[DummyRes].getName
  }

  class DummyRes extends Resource_ImplBase { def name = "DummyRes" }


  // Shared Resource

  def sharedDefaultParams = {
    object r extends SharedResource[DummyShared]("/test/data", Map("a".asInstanceOf[Object] -> "b".asInstanceOf[Object]))
    r.url must_== "/test/data"
    r.params must_== Map("a".asInstanceOf[Object] -> "b".asInstanceOf[Object])
  }

  def sharedParamToList = {
    object r extends SharedResource[DummyShared]("/test/data", Map("a".asInstanceOf[Object] -> "b".asInstanceOf[Object]))
    r.parameterList must_== Seq("a", "b")
  }

  def sharedBind = {
    object r extends SharedResource[DummyShared]("/test/data")
    val o = new DummyShared()
    r.bind(o)

    r.resource must_== o
  }

  def sharedBindUima = {
    object r extends SharedResource[DummyShared]("/test/data")
    val o = new DummyShared()
    r.setFromUima(o)

    r.resource must_== o
  }

  def sharedSetBinding = {
    object r extends SharedResource[DummyShared]("/test/data", Map("a".asInstanceOf[Object] -> "b".asInstanceOf[Object]))
    r := SharedBinding("/abc/def", Map("c".asInstanceOf[Object] -> "d".asInstanceOf[Object]))
    r.url must_== "/abc/def"
    r.params must_== Map("c".asInstanceOf[Object] -> "d".asInstanceOf[Object])
  }

  def sharedClassName = {
    object r extends SharedResource[DummyShared]("/test/data", Map("a".asInstanceOf[Object] -> "b".asInstanceOf[Object]))
    r.className.getName must_== classOf[DummyShared].getName
  }

  def sharedInterfaceName = {
    object r extends SharedResource[DummyShared]("/test/data", Map("a".asInstanceOf[Object] -> "b".asInstanceOf[Object]))
    r.interfaceName must_== classOf[DummyShared].getName
  }

  class DummyShared extends SharedResourceObject {
    def load(data: DataResource) = {}
    def name = "SharedDict"
  }
}
