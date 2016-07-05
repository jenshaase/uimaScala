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

class RegexTokenizerSpec extends Specification {

  "Regex Tokenizer" should {
    "split by x" in {
      val tokenizer: AnalysisEngine = new RegexTokenizer().
        config(
          _.regex := "x".r
        ).
        asAnalysisEngine

      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("HalloxWeltxlosxgehtsx")
      tokenizer.process(jcas)

      jcas.select[Token].size must be equalTo(4)
      jcas.selectByIndex[Token](0).getCoveredText must be equalTo ("Hallo")
      jcas.selectByIndex[Token](1).getCoveredText must be equalTo ("Welt")
      jcas.selectByIndex[Token](2).getCoveredText must be equalTo ("los")
      jcas.selectByIndex[Token](3).getCoveredText must be equalTo ("gehts")
    }

    "allow empty token" in {
      val tokenizer: AnalysisEngine = new RegexTokenizer().
        config(
          _.regex := "x".r,
          _.allowEmptyToken := true
        ).
        asAnalysisEngine

      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("HalloxWeltxlosxxgehts")
      tokenizer.process(jcas)

      jcas.select[Token].size must be equalTo(5)
      jcas.selectByIndex[Token](0).getCoveredText must be equalTo ("Hallo")
      jcas.selectByIndex[Token](1).getCoveredText must be equalTo ("Welt")
      jcas.selectByIndex[Token](2).getCoveredText must be equalTo ("los")
      jcas.selectByIndex[Token](3).getCoveredText must be equalTo ("")
      jcas.selectByIndex[Token](4).getCoveredText must be equalTo ("gehts")
    }
  }
}
