/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.toolkit.annotator

import org.specs2.mutable.Specification
import org.apache.uima.analysis_engine.AnalysisEngine
import org.uimafit.factory.AnalysisEngineFactory
import jenshaase.uimaScala.core.Implicits._
import jenshaase.uimaScala.toolkit.types.{Token, Stopword}
import java.io.File

class StopwordTaggerSpec extends Specification {

  "Stopword Tagger" should {
    val tokenizer: AnalysisEngine = new BreakIteratorTokenizer().asAnalysisEngine

    "add annotations for each stopword" in {
      val tagger = new StopwordTagger().
      	stopwordFile(new File("uima-toolkit/src/main/resources/stopwords/german.txt")).
      	asAnalysisEngine
      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("Hallo, alle zusammen. Wie geht es euch?")
      jcas.setDocumentLanguage("de")
      tokenizer.process(jcas)
      tagger.process(jcas)

      jcas.selectByIndex(classOf[Stopword], 0).getCoveredText must be equalTo("alle")
      jcas.selectByIndex(classOf[Stopword], 1).getCoveredText must be equalTo("Wie")
      jcas.selectByIndex(classOf[Stopword], 2).getCoveredText must be equalTo("es")
    }
  }

  "Stopword remover" should {
    val tokenizer: AnalysisEngine = new BreakIteratorTokenizer().asAnalysisEngine

    "remove stopword tokens" in {
      val tagger = new StopwordRemover().
      	stopwordFile(new File("uima-toolkit/src/main/resources/stopwords/german.txt")).
      	asAnalysisEngine
      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("Hallo, alle zusammen. Wie geht es euch?")
      jcas.setDocumentLanguage("de")
      tokenizer.process(jcas)
      tagger.process(jcas)

      jcas.selectByIndex(classOf[Token], 0).getCoveredText must be equalTo("Hallo")
      jcas.selectByIndex(classOf[Token], 1).getCoveredText must be equalTo(",")
      jcas.selectByIndex(classOf[Token], 2).getCoveredText must be equalTo("zusammen")
    }
  }
}