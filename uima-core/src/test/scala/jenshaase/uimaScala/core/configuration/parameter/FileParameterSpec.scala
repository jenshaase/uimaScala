/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.configuration.parameter

import org.specs2.Specification
import jenshaase.uimaScala.core.configuration._
import parameter._
import java.io.File

class FileParameterSpec extends Specification {
  def is =

    "This specification describes the file parameter" ^
      p ^
      "The file parameter can" ^
      "be set by string" ! setString ^
      "be set by file" ! setFile ^
      "be converted to string" ! convert ^
      end ^
      "The optional file parameter should" ^
      "have a default value" ! default ^
      end

  def setString = {
    val m = new FileParamMock

    m.fileParam.setFromString("/my/simple/path")
    m.fileParam.is.getAbsolutePath must be equalTo ("/my/simple/path")
  }

  def setFile = {
    val m = new FileParamMock

    m.fileParam.setFromAny(new File("/my/simple/path"))
    m.fileParam.is.getAbsolutePath must be equalTo ("/my/simple/path")
  }

  def convert = {
    val m = new FileParamMock

    m.fileParam(new File("/my/simple/path"))
    m.fileParam.asObject must be equalTo ("/my/simple/path".asInstanceOf[Object])
  }

  def default = {
    new FileParamMock().optFileParam.defaultValue must beNone
  }
}

class FileParamMock extends Configurable {
  object fileParam extends FileParameter(this)

  object optFileParam extends OptionalFileParameter(this)
}