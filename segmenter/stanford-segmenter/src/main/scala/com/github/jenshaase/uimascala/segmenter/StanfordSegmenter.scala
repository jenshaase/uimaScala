package com.github.jenshaase.uimascala.segmenter

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource
import edu.stanford.nlp.ling.TaggedWord
import scala.collection.JavaConversions._
import java.io.StringReader
import java.util.Properties
import edu.stanford.nlp.ling.{CoreLabel, Word}
import edu.stanford.nlp.international.spanish.process.SpanishTokenizer
import edu.stanford.nlp.international.arabic.process.ArabicTokenizer
import edu.stanford.nlp.international.french.process.FrenchTokenizer
import edu.stanford.nlp.trees.international.pennchinese.CHTBTokenizer
import edu.stanford.nlp.process.{WordToSentenceProcessor, Tokenizer, PTBTokenizer, CoreLabelTokenFactory}
import edu.stanford.nlp.ling.CoreAnnotations.{CharacterOffsetBeginAnnotation, CharacterOffsetEndAnnotation}

class StanfordSegmenter extends SCasAnnotator_ImplBase {

  object annotateToken extends Parameter[Boolean](true)
  object annotateSentence extends Parameter[Boolean](true)
  object fallbackLanguage extends Parameter[Option[String]](None) {
    override def mandatory_? = false
  }

  def process(jcas: JCas) = {
    if (annotateToken.is) annotateTokens(jcas)
    if (annotateSentence.is) annotateSentences(jcas)
  }

  def annotateTokens(jcas: JCas) {
    val text = jcas.getDocumentText
    val tokenizer = getTokenizer(jcas.getDocumentLanguage, text)

    var offsetInSentence = 0
    tokenizer.tokenize().foreach { token =>
      token match {
        case token: String =>
          offsetInSentence = skipWhitespace(text, offsetInSentence)

          if (text.startsWith(token, offsetInSentence)) {
            add(createToken(jcas, offsetInSentence, offsetInSentence + token.size))
            offsetInSentence = offsetInSentence + token.size
          } else {
            throw new Exception("Text mismatch in Tokenizer: " + token + " not found")
          }

        case label: CoreLabel =>
          val begin = label.beginPosition
          val end = label.endPosition
          add(createToken(jcas, begin, end))
          offsetInSentence = end

        case word: Word =>
          val token = word.word
          offsetInSentence = skipWhitespace(text, offsetInSentence)

          if (text.startsWith(token, offsetInSentence)) {
            add(createToken(jcas, offsetInSentence, offsetInSentence + token.size))
            offsetInSentence = offsetInSentence + token.size
          } else {
            throw new Exception("Text mismatch in Tokenizer: " + token + " not found")
          }
      }
    }
  }

  def annotateSentences(jcas: JCas) {
    val tokens = jcas.select[Token].map { token =>
      val label = new CoreLabel()
      label.setBeginPosition(token.getBegin)
      label.setEndPosition(token.getEnd)
      label.setWord(token.getCoveredText)
      label
    }.toList

    val proc = new WordToSentenceProcessor[CoreLabel]()
    proc.process(tokens).foreach { sentence =>
      add(createSentence(jcas, sentence.head.beginPosition, sentence.last.endPosition))
    }
  }

  protected def skipWhitespace(text: String, offset: Int): Int = {
    var newOffset = offset
    while (newOffset < text.size && Character.isWhitespace(text.charAt(newOffset))) {
      newOffset = newOffset + 1
    }
    newOffset
  }

  protected def createToken(cas: JCas, begin: Int, end: Int) =
    new Token(cas, begin, end)

  protected def createSentence(cas: JCas, begin: Int, end: Int) =
    new Sentence(cas, begin, end)

  protected def getTokenizer(lang: String, text: String): Tokenizer[_] = {
    getTokenizerFromLanguage(lang, text) match {
      case Some(tokenizer) => tokenizer
      case None =>
        fallbackLanguage.is.flatMap { lang => 
          getTokenizerFromLanguage(lang, text)
        }.getOrElse(
          throw new Exception("can not create tokenizer for language: " + lang)
        )
    }
  }

  private def getTokenizerFromLanguage(lang: String, text: String): Option[Tokenizer[_]] = 
    lang match {
      case "ar" => Some(ArabicTokenizer.newArabicTokenizer(new StringReader(text), new Properties()))
      case "en" => Some(new PTBTokenizer[CoreLabel](new StringReader(text), new CoreLabelTokenFactory(), "invertible"))
      case "es" => Some(SpanishTokenizer.factory(new CoreLabelTokenFactory(), null).getTokenizer(new StringReader(text)))
      case "fr" => Some(FrenchTokenizer.factory().getTokenizer(new StringReader(text), "tokenizeNLs=false"))
      case "zh" => Some(new CHTBTokenizer(new StringReader(text)))
      case _ => None
    }
}

