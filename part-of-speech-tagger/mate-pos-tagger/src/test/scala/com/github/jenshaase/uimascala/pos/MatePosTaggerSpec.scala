package com.github.jenshaase.uimascala.pos

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class MatePosTaggerSpec extends Specification {

  "MatePosTagger" should {
    "tag each word in a sentence" in {
      val tagger: AnalysisEngine = new MatePosTagger().
        config(
          _.model := SharedBinding[MatePosTaggerResource]("de/tudarmstadt/ukp/dkpro/core/matetools/lib/tagger-de-tiger.model")
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
  }
}
