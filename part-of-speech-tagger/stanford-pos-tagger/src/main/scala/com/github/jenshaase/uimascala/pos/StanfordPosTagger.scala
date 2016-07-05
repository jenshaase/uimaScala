package com.github.jenshaase.uimascala.pos

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource
import edu.stanford.nlp.ling.TaggedWord
import scala.collection.JavaConversions._
import edu.stanford.nlp.tagger.maxent.MaxentTagger

class MaxentTaggerResource extends SharedResourceObject {
  private var tagger: MaxentTagger = _

  def load(data: DataResource) {
    tagger = new MaxentTagger(data.getUri.toString)
  }

  def getTagger = tagger
}

class StanfordPosTagger extends SCasAnnotator_ImplBase {

  object model extends SharedResource[MaxentTaggerResource](MaxentTagger.DEFAULT_JAR_PATH)
  object maxTokensPerSentence extends Parameter[Option[Int]](None) {
    override def mandatory_? = false
  }

  def process(jcas: JCas) = {
    jcas.select[Sentence].foreach { sentence =>
      val tokens = jcas.selectCovered[Token](sentence)

      maxTokensPerSentence.is match {
        case None =>
          processTokens(jcas, tokens)
        case Some(n) if (n > 0 && tokens.size <= n) =>
          processTokens(jcas, tokens)
        case _ =>
      }
    }
  }

  def processTokens(jcas: JCas, tokens: Seq[Token]) {
    val words = tokens.map { token => new TaggedWord(token.getCoveredText) }
    val taggedWords = model.resource.getTagger.tagSentence(words)

    tokens.zipWithIndex.foreach { case (token, idx) =>
      val tag = taggedWords.get(idx).tag()

      val pos = new POS(jcas, token.getBegin, token.getEnd)
      pos.setName(tag)
      add(pos)

      token.setPos(pos)
    }
  }
}
