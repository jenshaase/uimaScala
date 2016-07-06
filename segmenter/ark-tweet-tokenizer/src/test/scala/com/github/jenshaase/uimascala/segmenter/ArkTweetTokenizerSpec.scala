package com.github.jenshaase.uimascala.segmenter

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class ArkTweetTokenizerSpec extends Specification {

  "Ark Tweet Tokenizer" should {
    "annotate all tokens in a tweet" in {
      val tokenizer: AnalysisEngine = new ArkTweetTokenizer().asAnalysisEngine

      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("This is a test &amp; a thing #hash #tag bit.ly/link")
      tokenizer.process(jcas)

      jcas.select[Token].size must be equalTo(12)
      jcas.selectByIndex[Token](0).getCoveredText must be equalTo ("This")
      jcas.selectByIndex[Token](1).getCoveredText must be equalTo ("is")
      jcas.selectByIndex[Token](2).getCoveredText must be equalTo ("a")
      jcas.selectByIndex[Token](3).getCoveredText must be equalTo ("test")
      jcas.selectByIndex[Token](4).getCoveredText must be equalTo ("&")
      jcas.selectByIndex[Token](5).getCoveredText must be equalTo ("amp")
      jcas.selectByIndex[Token](6).getCoveredText must be equalTo (";")
      jcas.selectByIndex[Token](7).getCoveredText must be equalTo ("a")
      jcas.selectByIndex[Token](8).getCoveredText must be equalTo ("thing")
      jcas.selectByIndex[Token](9).getCoveredText must be equalTo ("#hash")
      jcas.selectByIndex[Token](10).getCoveredText must be equalTo ("#tag")
      jcas.selectByIndex[Token](11).getCoveredText must be equalTo ("bit.ly/link")
    }

    "it should normalize a tweet" in {
      ArkTweetTokenizer.normalizeTweet("This is a test &amp; a thing #hash #tag bit.ly/link") must be equalTo (
        "This is a test & a thing #hash #tag bit.ly/link"
      )
    }
  }
}
