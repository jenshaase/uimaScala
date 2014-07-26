/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.examples.ex1

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.SCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas

class RoomNumberAnnotator extends SCasAnnotator_ImplBase {

  val yorktownPattern = """\b[0-4]\d-[0-2]\d\d\b""".r
  val hawthronePattern = """\b[G1-4][NS]-[A-Z]\d\d\b""".r

  def process(cas: JCas) = {
    val docText = cas.getDocumentText()

    yorktownPattern.findAllIn(docText).matchData.foreach { m ⇒
      cas.create[RoomNumber](
        _.setBegin(m.start),
        _.setEnd(m.end),
        _.setBuilding("Yorktown")
      )
    }

    hawthronePattern.findAllIn(docText).matchData.foreach { m ⇒
      val a = cas.annotate[RoomNumber](m.start, m.end)
      a.setBuilding("Hawthrone")
    }
  }
}
