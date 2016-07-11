package com.github.jenshaase.uimascala.lemmatizer

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class MateLemmatizerSpec extends Specification {

  "MateLemmatizer" should {
    "lemmatize each word in a sentence" in {
      val tagger: AnalysisEngine = new MateLemmatizer().
        config(
          _.model := SharedBinding[MateLemmatizerResource]("de/tudarmstadt/ukp/dkpro/core/matetools/lib/lemmatizer-de-tiger.model")
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

      jcas.select[Lemma].size must be equalTo(4)
      jcas.selectByIndex[Lemma](0).getCoveredText must be equalTo ("Hallo")
      jcas.selectByIndex[Lemma](1).getCoveredText must be equalTo ("Welt")
      jcas.selectByIndex[Lemma](2).getCoveredText must be equalTo ("Was")
      jcas.selectByIndex[Lemma](3).getCoveredText must be equalTo ("geht")
    }
  }
}
