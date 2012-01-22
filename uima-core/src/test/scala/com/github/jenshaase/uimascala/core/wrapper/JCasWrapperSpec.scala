/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.wrapper

import org.specs2.mutable.Specification
import com.github.jenshaase.uimascala.core._
import util.Helper
import org.apache.uima.jcas.tcas.{ DocumentAnnotation, Annotation }
import org.apache.uima.jcas.JCas

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
class JCasWrapperSpec extends Specification with Helper {

  class Token(cas: JCas, begin: Int, end: Int) extends Annotation(cas, begin, end)

  "JCasWrapper" should {

    "select annotation of same type" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      new Annotation(cas, 0, 4).addToIndexes
      new Annotation(cas, 5, 7).addToIndexes

      // Note: One Annotation and one DocumentAnnotation are default
      // to each new JCas
      cas.select[Annotation].size must be equalTo (3)
      cas.select[DocumentAnnotation].size must be equalTo (1)
    }

    "select annotation by index" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      new Annotation(cas, 0, 4).addToIndexes

      cas.selectByIndex[Annotation](1).getCoveredText must be equalTo ("This")
    }

    "select all anntation covered by another annotation" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      val a1 = new Annotation(cas, 0, 4)
      a1.addToIndexes
      val a2 = new Annotation(cas, 0, 1)
      a2.addToIndexes
      val a3 = new Annotation(cas, 1, 2)
      a3.addToIndexes

      cas.selectCovered[Annotation](a1).size must be equalTo (2)
      cas.selectCovered[Annotation](a1).get(0).getCoveredText must be equalTo ("T")
    }

    "check if a annotation is covered" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      val a1 = new Annotation(cas, 0, 4)
      a1.addToIndexes
      val a2 = new Annotation(cas, 0, 1)
      a2.addToIndexes
      val a3 = new Annotation(cas, 1, 2)
      a3.addToIndexes

      cas.isCovered[Annotation](a1) must beTrue
    }

    "select a single annotation" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      cas.selectSingle[Annotation].getCoveredText must be equalTo ("This is a text")
    }

    "select all preceding annotation" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      val a1 = new Annotation(cas, 0, 4)
      a1.addToIndexes
      val a2 = new Annotation(cas, 5, 7)
      a2.addToIndexes
      val a3 = new Annotation(cas, 8, 9)
      a3.addToIndexes

      val p1 = cas.selectPreceding[Annotation](a2, 1)
      p1.size must be equalTo (1)
      p1.head.getCoveredText must be equalTo (a1.getCoveredText)
    }

    "select all following annotation" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      val a1 = new Annotation(cas, 0, 4)
      a1.addToIndexes
      val a2 = new Annotation(cas, 5, 7)
      a2.addToIndexes
      val a3 = new Annotation(cas, 8, 9)
      a3.addToIndexes

      val p1 = cas.selectFollowing[Annotation](a2, 1)
      p1.size must be equalTo (1)
      p1.head.getCoveredText must be equalTo (a3.getCoveredText)
    }

    "checks if an annotation type exists" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      cas.exists[Annotation] must beTrue
    }
  }
}