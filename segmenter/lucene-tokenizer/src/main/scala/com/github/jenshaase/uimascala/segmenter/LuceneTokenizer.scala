package com.github.jenshaase.uimascala.segmenter

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import java.text.BreakIterator
import org.apache.lucene.analysis.standard.StandardTokenizer
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute

class LuceneTokenizer extends SCasAnnotator_ImplBase {

  def process(jcas: JCas) = {
    val bi = BreakIterator.getSentenceInstance()
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
    val tokenizer = new StandardTokenizer()
    tokenizer.setReader(new java.io.StringReader(sentence))
    tokenizer.reset()
    while (tokenizer.incrementToken()) {
      val tokenOffset = tokenizer.getAttribute(classOf[OffsetAttribute])
      add(createToken(jcas, offset + tokenOffset.startOffset, offset + tokenOffset.endOffset))
    }
    tokenizer.end()
    tokenizer.close()
  }

  protected def createSentence(cas: JCas, begin: Int, end: Int) =
    new Sentence(cas, begin, end)

  protected def createToken(cas: JCas, begin: Int, end: Int) =
    new Token(cas, begin, end)
}
