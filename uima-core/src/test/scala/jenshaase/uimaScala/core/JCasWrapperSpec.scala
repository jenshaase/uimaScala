/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import org.specs2.mutable.Specification
import util.Helper
import Implicits._
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
      cas.select(classOf[Annotation]).size must be equalTo (3)
      cas.select(classOf[DocumentAnnotation]).size must be equalTo (1)
    }

    "select annotation by index" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      new Annotation(cas, 0, 4).addToIndexes

      cas.selectByIndex(classOf[Annotation], 1).getCoveredText must be equalTo ("This")
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

      cas.selectCovered(classOf[Annotation], a1).size must be equalTo (2)
      cas.selectCovered(classOf[Annotation], a1).get(0).getCoveredText must be equalTo ("T")
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

      cas.isCovered(a1, classOf[Annotation]) must beTrue
    }

    "select a single annotation" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      cas.selectSingle(classOf[Annotation]).getCoveredText must be equalTo ("This is a text")
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

      val p1 = cas.selectPreceding(classOf[Annotation], a2, 1)
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

      val p1 = cas.selectFollowing(classOf[Annotation], a2, 1)
      p1.size must be equalTo (1)
      p1.head.getCoveredText must be equalTo (a3.getCoveredText)
    }

    "checks if an annotation type exists" in {
      val cas = newJCas
      cas.setDocumentText("This is a text")

      cas.exists(classOf[Annotation]) must beTrue
    }
  }
}