/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.toolkit.annotator

import java.util.Locale
import jenshaase.uimaScala.core._
import jenshaase.uimaScala.toolkit.types.{ Token, Sentence }
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

      jcas.selectByIndex(classOf[Sentence], 0).getCoveredText must be equalTo ("Hallo, alle zusammen.")
      jcas.selectByIndex(classOf[Sentence], 1).getCoveredText must be equalTo ("Wie geht es euch?")
    }

    "split german words" in {
      val jcas = germanTokenizer.newJCas()
      jcas.setDocumentText("Hallo, alle zusammen. Wie geht es euch?")
      germanTokenizer.process(jcas)

      jcas.selectByIndex(classOf[Token], 0).getCoveredText must be equalTo ("Hallo")
      jcas.selectByIndex(classOf[Token], 1).getCoveredText must be equalTo (",")
      jcas.selectByIndex(classOf[Token], 2).getCoveredText must be equalTo ("alle")
      jcas.selectByIndex(classOf[Token], 3).getCoveredText must be equalTo ("zusammen")
    }

    "split english words when document language is set" in {
      val jcas = germanTokenizer.newJCas()
      jcas.setDocumentText("What's up? Once again")
      jcas.setDocumentLanguage("en");
      germanTokenizer.process(jcas)

      jcas.selectByIndex(classOf[Token], 0).getCoveredText must be equalTo ("What's")
      jcas.selectByIndex(classOf[Token], 1).getCoveredText must be equalTo ("up")
      jcas.selectByIndex(classOf[Token], 2).getCoveredText must be equalTo ("?")
      jcas.selectByIndex(classOf[Token], 3).getCoveredText must be equalTo ("Once")
      jcas.selectByIndex(classOf[Token], 4).getCoveredText must be equalTo ("again")
    }
  }
}