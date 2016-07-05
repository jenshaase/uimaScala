/**
  * Copyright (C) 2011 Jens Haase
  */
package com.github.jenshaase.uimascala.segmenter

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class WhitespaceTokenizerSpec extends Specification {

  "Whitespace Tokenizer" should {
    "split by whitespace" in {
      val tokenizer: AnalysisEngine = new WhitespaceTokenizer().asAnalysisEngine

      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("Hallo Welt  los\ngehts ")
      tokenizer.process(jcas)

      jcas.select[Token].size must be equalTo(4)
      jcas.selectByIndex[Token](0).getCoveredText must be equalTo ("Hallo")
      jcas.selectByIndex[Token](1).getCoveredText must be equalTo ("Welt")
      jcas.selectByIndex[Token](2).getCoveredText must be equalTo ("los")
      jcas.selectByIndex[Token](3).getCoveredText must be equalTo ("gehts")
    }
  }
}
