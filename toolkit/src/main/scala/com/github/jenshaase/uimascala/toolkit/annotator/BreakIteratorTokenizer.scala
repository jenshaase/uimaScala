/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.annotator

import java.text.BreakIterator
import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.toolkit.configuration._
import com.github.jenshaase.uimascala.toolkit.description._
import org.apache.uima.jcas.JCas
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.fit.factory.AnalysisEngineFactory

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
