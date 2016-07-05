/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.segmenter

import java.text.BreakIterator
import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.jcas.JCas
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.fit.factory.AnalysisEngineFactory

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
class BreakIteratorSegmenter extends SCasAnnotator_ImplBase {

  object locale extends Parameter[Locale](Locale.getDefault)

  def process(jcas: JCas) = {
    val bi = BreakIterator.getSentenceInstance(getLocale(jcas))
    bi.setText(jcas.getDocumentText)

    var last = bi.first
    var cur = bi.next
    while (cur != BreakIterator.DONE) {
      val sentence = addIfNotEmpty(createSentence(jcas, last, cur).trim)
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
      addIfNotEmpty(createToken(jcas, last + offset, cur + offset).trim)

      last = cur
      cur = bi.next
    }
  }

  protected def createSentence(cas: JCas, begin: Int, end: Int) =
    new Sentence(cas, begin, end)

  protected def createToken(cas: JCas, begin: Int, end: Int) =
    new Token(cas, begin, end)

  protected def getLocale(jcas: JCas): Locale = {
    val l = jcas.getDocumentLanguage()
    if (l != null && l != "x-unspecified") {
      return new Locale(l)
    }

    locale.is
  }
}
