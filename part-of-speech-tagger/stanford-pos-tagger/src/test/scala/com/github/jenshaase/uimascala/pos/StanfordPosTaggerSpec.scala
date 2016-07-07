/**
  * Copyright (C) 2011 Jens Haase
  */
package com.github.jenshaase.uimascala.pos

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class StanfordPosTaggerSpec extends Specification {

  "StanfordPosTagger" should {
    "tag each word in a sentence" in {
      val tagger: AnalysisEngine = new StanfordPosTagger().
        config(
          _.model := SharedBinding[MaxentTaggerResource]("edu/stanford/nlp/models/pos-tagger/german/german-fast.tagger")
        ).
        asAnalysisEngine

      val jcas = tagger.newJCas()
      jcas.setDocumentText("Hallo Welt! Was geht?")
      jcas.annotate[Sentence](0, 10)
      jcas.annotate[Sentence](12, 20)
      jcas.annotate[Token](0, 5)
      jcas.annotate[Token](6, 10)
      jcas.annotate[Token](12, 15)
      jcas.annotate[Token](16, 20)
      tagger.process(jcas)

      jcas.select[POS].size must be equalTo(4)
      jcas.selectByIndex[POS](0).getCoveredText must be equalTo ("Hallo")
      jcas.selectByIndex[POS](1).getCoveredText must be equalTo ("Welt")
      jcas.selectByIndex[POS](2).getCoveredText must be equalTo ("Was")
      jcas.selectByIndex[POS](3).getCoveredText must be equalTo ("geht")
    }

    "tag each word in a sentence if the sentences is short enough" in {
      val tagger: AnalysisEngine = new StanfordPosTagger().
        config(
          _.model := SharedBinding[MaxentTaggerResource]("edu/stanford/nlp/models/pos-tagger/german/german-fast.tagger"),
          _.maxTokensPerSentence := Some(2)
        ).
        asAnalysisEngine

      val jcas = tagger.newJCas()
      jcas.setDocumentText("Hallo Welt! Was geht heute?")
      jcas.annotate[Sentence](0, 10)
      jcas.annotate[Sentence](12, 26)
      jcas.annotate[Token](0, 5)
      jcas.annotate[Token](6, 10)
      jcas.annotate[Token](12, 15)
      jcas.annotate[Token](16, 20)
      jcas.annotate[Token](21, 26)
      tagger.process(jcas)

      jcas.select[POS].size must be equalTo(2)
      jcas.selectByIndex[POS](0).getCoveredText must be equalTo ("Hallo")
      jcas.selectByIndex[POS](1).getCoveredText must be equalTo ("Welt")
    }

    "test" in {
      val tagger: AnalysisEngine = new StanfordPosTagger().
        config(
          _.model := SharedBinding[MaxentTaggerResource]("edu/stanford/nlp/models/pos-tagger/german/german-fast.tagger")
        ).
        asAnalysisEngine

      val jcas = tagger.newJCas()
      jcas.setDocumentText("Wie alt bist du?")
      jcas.annotate[Sentence](0, 16);
      jcas.annotate[Token](0, 3)
      jcas.annotate[Token](4, 7)
      jcas.annotate[Token](8, 12)
      jcas.annotate[Token](13, 15)
      jcas.annotate[Token](15, 16)
      tagger.process(jcas)

      println(jcas.select[POS].map(_.getName).toList)

      true must be equalTo (true)
    }
  }
}
