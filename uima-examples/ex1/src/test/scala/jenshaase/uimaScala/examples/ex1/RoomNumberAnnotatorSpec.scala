/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.examples.ex1

import jenshaase.uimaScala.core._
import jenshaase.uimaScala.examples.ex1.types._
import org.specs2.mutable.Specification

class RoomNumberAnnotatorSpec extends Specification {

  "Room Number Annotator" should {
    val ann = new RoomNumberAnnotator().asAnalysisEngine

    "mark Yorktown rooms" in {
      val cas = ann.newJCas()
      cas.setDocumentText("9:00AM-5:00PM in Yorktown 11-176: Meeting")
      ann.process(cas)

      val a = cas.selectByIndex(classOf[RoomNumber], 0)
      a.getCoveredText must be equalTo ("11-176")
      a.getBuilding must be equalTo ("Yorktown")
    }

    "mark Hawthorne rooms" in {
      val cas = ann.newJCas()
      cas.setDocumentText("9:00AM-5:00PM in HAW GN-S23: Meeting")
      ann.process(cas)

      val a = cas.selectByIndex(classOf[RoomNumber], 0)
      a.getCoveredText must be equalTo ("GN-S23")
      a.getBuilding must be equalTo ("Hawthrone")
    }
  }
}