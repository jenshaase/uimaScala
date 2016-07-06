package com.github.jenshaase.uimascala.segmenter

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class StanfordSegmenterSpec extends Specification {

  "StanfordSegmenter" should {
    "segment english sentences and tokens" in {
      val segmenter: AnalysisEngine = new StanfordSegmenter().
        asAnalysisEngine

      val jcas = segmenter.newJCas()
      jcas.setDocumentText("This is a text. Here we are! ")
      jcas.setDocumentLanguage("en")
      segmenter.process(jcas)

      jcas.select[Token].size must be equalTo(9)
      jcas.selectByIndex[Token](0).getCoveredText must be equalTo ("This")
      jcas.selectByIndex[Token](1).getCoveredText must be equalTo ("is")
      jcas.selectByIndex[Token](2).getCoveredText must be equalTo ("a")
      jcas.selectByIndex[Token](3).getCoveredText must be equalTo ("text")
      jcas.selectByIndex[Token](4).getCoveredText must be equalTo (".")
      jcas.selectByIndex[Token](5).getCoveredText must be equalTo ("Here")
      jcas.selectByIndex[Token](6).getCoveredText must be equalTo ("we")
      jcas.selectByIndex[Token](7).getCoveredText must be equalTo ("are")
      jcas.selectByIndex[Token](8).getCoveredText must be equalTo ("!")

      jcas.select[Sentence].size must be equalTo(2)
      jcas.selectByIndex[Sentence](0).getCoveredText must be equalTo ("This is a text.")
      jcas.selectByIndex[Sentence](1).getCoveredText must be equalTo ("Here we are!")
    }

    "segment french sentences and tokens" in {
      val segmenter: AnalysisEngine = new StanfordSegmenter().
        asAnalysisEngine

      val jcas = segmenter.newJCas()
      jcas.setDocumentText("Bonjour à tous. C'est parti!")
      jcas.setDocumentLanguage("fr")
      segmenter.process(jcas)

      jcas.select[Token].size must be equalTo(8)
      jcas.selectByIndex[Token](0).getCoveredText must be equalTo ("Bonjour")
      jcas.selectByIndex[Token](1).getCoveredText must be equalTo ("à")
      jcas.selectByIndex[Token](2).getCoveredText must be equalTo ("tous")
      jcas.selectByIndex[Token](3).getCoveredText must be equalTo (".")
      jcas.selectByIndex[Token](4).getCoveredText must be equalTo ("C'")
      jcas.selectByIndex[Token](5).getCoveredText must be equalTo ("est")
      jcas.selectByIndex[Token](6).getCoveredText must be equalTo ("parti")
      jcas.selectByIndex[Token](7).getCoveredText must be equalTo ("!")

      jcas.select[Sentence].size must be equalTo(2)
      jcas.selectByIndex[Sentence](0).getCoveredText must be equalTo ("Bonjour à tous.")
      jcas.selectByIndex[Sentence](1).getCoveredText must be equalTo ("C'est parti!")
    }
  }
}
