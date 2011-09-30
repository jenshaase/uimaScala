/**
 * Copyright (C) 2011 Jens Haase
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
      addIfNotEmpty(new Token(jcas, last + offset, cur + offset).trim)

      last = cur
      cur = bi.next
    }
  }
}