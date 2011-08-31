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

import org.apache.uima.jcas.JCas
import java.text.BreakIterator
import java.util.Locale
import jenshaase.uimaScala.toolkit.types._
import org.uimafit.descriptor.ConfigurationParameter
import jenshaase.uimaScala.core.SCasAnnotator_ImplBase
import jenshaase.uimaScala.toolkit.configuration._
import org.uimafit.factory.AnalysisEngineFactory

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
class BreakIteratorTokenizer extends SCasAnnotator_ImplBase with LocaleConfig {
  
  def process(jcas: JCas) = {
    val bi = BreakIterator.getSentenceInstance(getLocale(jcas))
    bi.setText(jcas.getDocumentText)
    
    var last = bi.first
    var cur = bi.next
    while (cur != BreakIterator.DONE) {
      val sentence = addIfNotEmpty(new Sentence(jcas, last, cur).trim)
      processSentence(jcas, sentence.getCoveredText, last)
      
      last = cur
      cur = bi.next
    }
  }
  
  def processSentence(jcas: JCas, sentence: String, offset: Int) = {
    val bi = BreakIterator.getWordInstance(getLocale(jcas))
    bi.setText(sentence)
    
    var last = bi.first
    var cur = bi.next
    while (cur != BreakIterator.DONE) {
      addIfNotEmpty(new Token(jcas, last+offset, cur+offset).trim)
      
      last = cur
      cur = bi.next
    }
  }
}