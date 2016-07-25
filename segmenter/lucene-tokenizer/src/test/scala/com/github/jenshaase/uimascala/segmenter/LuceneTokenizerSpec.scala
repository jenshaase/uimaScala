package com.github.jenshaase.uimascala.segmenter

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class LuceneTokenizerSpec extends Specification {

  "Lucene Tokenizer" should {
    val tokenizer: AnalysisEngine = new LuceneTokenizer().asAnalysisEngine

    "split in sentences" in {
      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("Hallo, alle zusammen. Wie geht es euch?")
      tokenizer.process(jcas)

      jcas.selectByIndex[Sentence](0).getCoveredText must be equalTo ("Hallo, alle zusammen.")
      jcas.selectByIndex[Sentence](1).getCoveredText must be equalTo ("Wie geht es euch?")
    }

    "split words" in {
      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("Hallo, alle zusammen. Wie geht es euch?")
      tokenizer.process(jcas)

      jcas.selectByIndex[Token](0).getCoveredText must be equalTo ("Hallo")
      jcas.selectByIndex[Token](1).getCoveredText must be equalTo ("alle")
      jcas.selectByIndex[Token](2).getCoveredText must be equalTo ("zusammen")
    }
  }
}
