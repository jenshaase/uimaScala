package com.github.jenshaase.uimascala.segmenter

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import cmu.arktweetnlp.Twokenize
import scala.collection.JavaConversions._

object ArkTweetTokenizer {
  def normalizeTweet(tweet: String): String =
    Twokenize.normalizeTextForTagger(tweet)
}

class ArkTweetTokenizer extends SCasAnnotator_ImplBase {

  def process(jcas: JCas) = {
    val txt = jcas.getDocumentText

    Twokenize.tokenize(txt).foldLeft(0) { (offset, token) =>
      val start = txt.indexOf(token, offset);
      val end = start + token.length
      add(createToken(jcas, start, end))
      end
    }
  }

  def createToken(cas: JCas, begin: Int, end: Int) =
    new Token(cas, begin, end)
}
