/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.examples.ex1

import com.github.jenshaase.uimascala.core.SCasAnnotator_ImplBase
import com.github.jenshaase.uimascala.examples.ex1.types._
import org.apache.uima.jcas.JCas

class RoomNumberAnnotator extends SCasAnnotator_ImplBase {

  val yorktownPattern = """\b[0-4]\d-[0-2]\d\d\b""".r
  val hawthronePattern = """\b[G1-4][NS]-[A-Z]\d\d\b""".r

  def process(cas: JCas) = {
    val docText = cas.getDocumentText()

    yorktownPattern.findAllIn(docText).matchData.foreach { m ⇒
      val annotation = new RoomNumber(cas)
      annotation.setBegin(m.start)
      annotation.setEnd(m.end)
      annotation.setBuilding("Yorktown")
      annotation.addToIndexes()
    }

    hawthronePattern.findAllIn(docText).matchData.foreach { m ⇒
      val annotation = new RoomNumber(cas, m.start, m.end)
      annotation.setBuilding("Hawthrone")
      annotation.addToIndexes()
    }
  }
}