/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.annotator

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.toolkit.types.{ Token, Sentence }
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.util.JCasUtil

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */

class BreakIteratorSpec extends Specification {

  "Break Iterator" should {
    val germanTokenizer: AnalysisEngine = new BreakIteratorTokenizer().config(
      _.locale := Locale.GERMAN).asAnalysisEngine

    "split german sentences" in {
      val jcas = germanTokenizer.newJCas()
      jcas.setDocumentText("Hallo, alle zusammen. Wie geht es euch?")
      germanTokenizer.process(jcas)

      jcas.selectByIndex[Sentence](0).getCoveredText must be equalTo ("Hallo, alle zusammen.")
      jcas.selectByIndex[Sentence](1).getCoveredText must be equalTo ("Wie geht es euch?")
    }

    "split german words" in {
      val jcas = germanTokenizer.newJCas()
      jcas.setDocumentText("Hallo, alle zusammen. Wie geht es euch?")
      germanTokenizer.process(jcas)

      jcas.selectByIndex[Token](0).getCoveredText must be equalTo ("Hallo")
      jcas.selectByIndex[Token](1).getCoveredText must be equalTo (",")
      jcas.selectByIndex[Token](2).getCoveredText must be equalTo ("alle")
      jcas.selectByIndex[Token](3).getCoveredText must be equalTo ("zusammen")
    }

    "split english words when document language is set" in {
      val jcas = germanTokenizer.newJCas()
      jcas.setDocumentText("What's up? Once again")
      jcas.setDocumentLanguage("en");
      germanTokenizer.process(jcas)

      jcas.selectByIndex[Token](0).getCoveredText must be equalTo ("What's")
      jcas.selectByIndex[Token](1).getCoveredText must be equalTo ("up")
      jcas.selectByIndex[Token](2).getCoveredText must be equalTo ("?")
      jcas.selectByIndex[Token](3).getCoveredText must be equalTo ("Once")
      jcas.selectByIndex[Token](4).getCoveredText must be equalTo ("again")
    }
  }
}