package com.github.jenshaase.uimascala.segmenter

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification

class OpenNlpSegmenterSpec extends Specification {

  "Open Nlp Segmenter" should {
    "add sentence and token annotations" in {
      val segmenter: AnalysisEngine = new OpenNlpSegmenter().
        config(
          _.sentenceModel := SharedBinding[OpenNlpSentenceSegmenterResource]("/de/tudarmstadt/ukp/dkpro/core/opennlp/lib/sentence-de-maxent.bin"),
          _.tokenModel := SharedBinding[OpenNlpTokenSegmenterResource]("/de/tudarmstadt/ukp/dkpro/core/opennlp/lib/token-de-maxent.bin")
        ).
        asAnalysisEngine

      val jcas = segmenter.newJCas()
      jcas.setDocumentText("Wie alt bist du?")
      segmenter.process(jcas)

      val sentences = jcas.select[Sentence].toVector
      sentences.size must be equalTo(1)
      sentences(0).getCoveredText must be equalTo(jcas.getDocumentText)

      val tokens = jcas.select[Token].toVector
      tokens.size must be equalTo(5)
      tokens(0).getCoveredText must be equalTo("Wie")
      tokens(1).getCoveredText must be equalTo("alt")
      tokens(2).getCoveredText must be equalTo("bist")
      tokens(3).getCoveredText must be equalTo("du")
      tokens(4).getCoveredText must be equalTo("?")
    }
  }
}
