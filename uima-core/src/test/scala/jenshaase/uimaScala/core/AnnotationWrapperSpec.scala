/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import org.specs2.mutable.Specification
import Implicits._
import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.{ TypePrioritiesFactory, TypeSystemDescriptionFactory }
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import util.Helper

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
class AnnotationWrapperSpec extends Specification with Helper {

  "Annotation Wrapper" should {

    "trim a annotation" in {
      val cas = newJCas
      cas.setDocumentText("This is text")

      val a = new Annotation(cas, 4, 8)
      a.getCoveredText must be equalTo (" is ")
      a.trim.getCoveredText must be equalTo ("is")
    }

    "check if a annotation is empty" in {
      new Annotation(newJCas, 0, 0).isEmpty must beTrue
      new Annotation(newJCas, 0, 1).isEmpty must beFalse
    }
  }
}