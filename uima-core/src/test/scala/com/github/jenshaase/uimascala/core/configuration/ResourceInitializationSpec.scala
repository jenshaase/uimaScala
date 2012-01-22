/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.configuration

import org.specs2.Specification
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource
import org.apache.uima.resource.Resource_ImplBase

class ResourceInitializationSpec extends Specification {
  def is =

    "This specification describes the resource initialization" ^
      p ^
      "The ResourceMock class should" ^
      "have 4 resource objects" ! nbResource(4) ^
      "have a resource called 'dictionary'" ! hasResource("dictionary") ^
      "have a resource called 'name'" ! hasResource("name") ^
      "have a resource called 'stopwords'" ! hasResource("stopwords") ^
      "have a resource called 'optName'" ! hasResource("optName") ^
      "return the correct dictionary resource" ! todo ^
      "return the correct name resource" ! todo ^
      "return the correct stopwords resource" ! todo ^
      "return the correct optName resource" ! todo ^
      end

  def nbResource(count: Int) =
    new ResourceMock().resources.size must be equalTo (count)

  def hasResource(name: String) =
    new ResourceMock().resources.map(_.name).contains(name) must beTrue

}

class ResourceMock extends Configurable with ResourceInitialization {

  object dictionary extends SharedResource[SharedDict]("/path/to/noWhere")

  object name extends Resource[SharedName]

  object stopwords extends SharedResource[SharedStopword]("/path/to/noWhere")

  object optName extends Resource[SharedOptName]
}

class SharedName extends Resource_ImplBase {
  def name = "myName"
}

class SharedOptName extends Resource_ImplBase {
  def name = "myOptName"
}

class SharedDict extends SharedResourceObject {

  def load(data: DataResource) =
    data.getUri.toString

  def getDict = "dict"
}

class SharedStopword extends SharedResourceObject {

  def load(data: DataResource) =
    data.getUri.toString

  def getStopword = "stopword"
}
