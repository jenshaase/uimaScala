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
import org.uimafit.factory.AnalysisEngineFactory
import org.apache.uima.analysis_engine.AnalysisEngine
import org.uimafit.util.JCasUtil
import jenshaase.uimaScala.toolkit.types.{Token, Sentence}
import jenshaase.uimaScala.core.Implicits._

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */

class BreakIteratorSpec extends Specification {
  
  "Break Iterator" should {
    val tokenizer: AnalysisEngine = AnalysisEngineFactory.createPrimitive(classOf[BreakIteratorTokenizer])

    "split german sentences" in {
      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("Hallo, alle zusammen. Wie geht es euch?")
      jcas.setDocumentLanguage("de")
      tokenizer.process(jcas)
      
      JCasUtil.selectByIndex(jcas, classOf[Sentence], 0).getCoveredText must be equalTo("Hallo, alle zusammen.")
      JCasUtil.selectByIndex(jcas, classOf[Sentence], 1).getCoveredText must be equalTo("Wie geht es euch?")
    }
    
    "split german words" in {
      val jcas = tokenizer.newJCas()
      jcas.setDocumentText("Hallo, alle zusammen. Wie geht es euch?")
      jcas.setDocumentLanguage("de")
      tokenizer.process(jcas)
      
      jcas.selectByIndex(classOf[Token], 0).getCoveredText must be equalTo("Hallo")
      jcas.selectByIndex(classOf[Token], 1).getCoveredText must be equalTo(",")
      jcas.selectByIndex(classOf[Token], 2).getCoveredText must be equalTo("alle")
      jcas.selectByIndex(classOf[Token], 3).getCoveredText must be equalTo("zusammen")
    }
  }
}