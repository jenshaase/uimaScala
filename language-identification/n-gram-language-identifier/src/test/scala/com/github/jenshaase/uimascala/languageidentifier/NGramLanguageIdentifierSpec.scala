package com.github.jenshaase.uimascala.languageidentifier

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification

class NGramLanguageIdentifierSpec extends Specification {

  "The ngram language idenifier" should {
    "detect the german language" in {
      val analyser: AnalysisEngine = new NGramLanguageIdentifier().asAnalysisEngine

      val jcas = analyser.newJCas()
      jcas.setDocumentText("Das ist ein Text in deutscher Sprache")
      analyser.process(jcas)

      jcas.getDocumentLanguage must be equalTo("de")
    }

    "detect the english language" in {
      val analyser: AnalysisEngine = new NGramLanguageIdentifier().asAnalysisEngine

      val jcas = analyser.newJCas()
      jcas.setDocumentText("This is a english text with so information.")
      analyser.process(jcas)

      jcas.getDocumentLanguage must be equalTo("en")
    }

    "detect the german language in short text snippets" in {
      val analyser: AnalysisEngine = new NGramLanguageIdentifier().config(_.shortText := true).asAnalysisEngine

      val jcas = analyser.newJCas()
      jcas.setDocumentText("Das ist ein Text in deutscher Sprache")
      analyser.process(jcas)

      jcas.getDocumentLanguage must be equalTo("de")
    }

    "detect the english language in short text snippets" in {
      val analyser: AnalysisEngine = new NGramLanguageIdentifier().config(_.shortText := true).asAnalysisEngine

      val jcas = analyser.newJCas()
      jcas.setDocumentText("This is a english text with so information.")
      analyser.process(jcas)

      jcas.getDocumentLanguage must be equalTo("en")
    }
  }
}
