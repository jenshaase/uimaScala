/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration.parameter

import org.specs2.Specification
import jenshaase.uimaScala.core.configuration._
import parameter._
import java.util.regex.Pattern

class PatternParameterSpec extends Specification {
  def is =

    "This specification describes the pattern parameter" ^
      p ^
      "The pattern parameter can" ^
      "be set by string" ! setString ^
      "be set by pattern" ! setPattern ^
      "be converted to string" ! convertString ^
      end ^
      "The optinal pattern parameter should" ^
      "have a default value" ! default ^
      end

  def setString = {
    val m = new PatternParamMock()

    m.patternParam.setFromString(".*\\.txt") must beRight
    m.patternParam.is.pattern must be equalTo (".*\\.txt")
  }

  def setPattern = {
    val m = new PatternParamMock()

    m.patternParam.setFromAny(Pattern.compile(".*\\.txt")) must beRight
    m.patternParam.is.pattern must be equalTo (".*\\.txt")
  }

  def convertString = {
    val m = new PatternParamMock().patternParam(Pattern.compile(".*\\.txt"))

    m.patternParam.asObject must be equalTo (".*\\.txt".asInstanceOf[Object])
  }

  def default = {
    val m = new PatternParamMock()

    m.optPatternParam.is.map(_.pattern) must be equalTo (Some(".*\\.txt"))
  }
}

class PatternParamMock extends Configurable {
  object patternParam extends PatternParameter(this)

  object optPatternParam extends OptionalPatternParameter(this) {
    override def defaultValue = Some(Pattern.compile(".*\\.txt"))
  }
}