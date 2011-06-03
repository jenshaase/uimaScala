/*
 * Copyright (C) 2011 by Jens Haase <je.haase@googlemail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenshaase.uimaScala.toolkit.annotator

import org.specs2.mutable.Specification
import org.apache.uima.analysis_engine.AnalysisEngine
import org.uimafit.factory.AnalysisEngineFactory
import jenshaase.uimaScala.core.Implicits._
import jenshaase.uimaScala.toolkit.types.{Token, Stopword}

class StopwordTaggerSpec extends Specification {

  "Stopword Tagger" should {
    val tokenizer: AnalysisEngine = AnalysisEngineFactory.createPrimitive(classOf[BreakIteratorTokenizer])

    "add annotations for each stopword" in {
      val tagger = StopwordTagger("uima-toolkit/src/main/resources/stopwords/german.txt")
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
    val tokenizer: AnalysisEngine = AnalysisEngineFactory.createPrimitive(classOf[BreakIteratorTokenizer])

    "remove stopword tokens" in {
      val tagger = StopwordRemover("uima-toolkit/src/main/resources/stopwords/german.txt")
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