/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.configuration

import org.specs2.Specification
import com.github.jenshaase.uimascala.core.configuration._

class ConfigurationInitalizationSpec extends Specification {
  def is =

    "This is a specification to check the configuration system" ^
      p ^
      "ConfigMock should" ^
      "have 4 parameters" ! nbParams(4) ^
      "have a parameter called 'stringParam'" ! hasParam("stringParam") ^
      "have a parameter called 'optStringParam'" ! hasParam("stringListParam") ^
      "have a parameter called 'intParam'" ! hasParam("intParam") ^
      "have a parameter called 'optIntParam'" ! hasParam("intListParam") ^
      "generate a key value list of parameters" ! todo
  end

  def hasParam(name: String) =
    new ConfigMock().parameters.map(_.name).contains(name) must beTrue

  def nbParams(count: Int) =
    new ConfigMock().parameters.size must be equalTo (count)

  def createKeyValues = {
    val config = new ConfigMock()
    config.stringParam := "Test"
    config.intParam := 100

    config.parameterKeyValues.toList.
      sliding(2, 2).map(l ⇒ Pair(l(0).asInstanceOf[String], l(1))).toList.sortBy(_._1) must be equalTo (List(
        ("intListParam", Array(1, 2).asInstanceOf[Object]),
        ("intParam", 100.asInstanceOf[Object]),
        ("stringListParam", Array("ab", "cd").asInstanceOf[Object]),
        ("stringParam", "Test".asInstanceOf[Object])))
  }
}

class ConfigMock extends Configurable with ConfigurationInitialization {
  object stringParam extends Parameter[String]("test")
  object stringListParam extends Parameter[List[String]](List("ab", "cd"))

  object intParam extends Parameter[Int](1)
  object intListParam extends Parameter[List[Int]](List(1, 2))

  object somethingElse {
    def someMethod = "Anything"
  }
}