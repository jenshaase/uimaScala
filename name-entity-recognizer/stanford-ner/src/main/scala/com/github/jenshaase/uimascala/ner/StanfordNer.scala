package com.github.jenshaase.uimascala.ner

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource
import edu.stanford.nlp.ling.TaggedWord
import edu.stanford.nlp.ie.crf.CRFClassifier
import edu.stanford.nlp.util.CoreMap
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.ling.CoreAnnotations
import scala.collection.JavaConversions._
import java.util.zip.GZIPInputStream


class StanfordNerResource extends SharedResourceObject {
  private var tagger: CRFClassifier[CoreMap] = _

  def load(data: DataResource) {
    val uri = data.getUri.toString

    if (new java.io.File(uri).exists) {
      tagger = CRFClassifier.getClassifier[CoreMap](new java.io.File(uri))
    } else {
      val resourceUri = if (uri.startsWith("/")) uri else "/" + uri
      val resource = this.getClass.getResource(resourceUri)

      val is = if (uri.endsWith(".gz")) {
        new GZIPInputStream(resource.openStream)
      } else {
        resource.openStream
      }

      tagger = CRFClassifier.getClassifier[CoreMap](is)
    }
  }

  def getTagger = tagger
}

class StanfordNer extends SCasAnnotator_ImplBase {

  object model extends SharedResource[StanfordNerResource]("")

  def process(jcas: JCas) = {
    jcas.select[Sentence].foreach { sentence =>
      val tokens = jcas.selectCovered[Token](sentence).toVector

      model.resource.getTagger.
        classifySentence(tokens.map(tokenToCoreLabel _)).
        foldLeft[(Int, Int, Option[String])](-1, -1, None) { case ((begin, end, currentType), taggedWord) =>
          val tokenType = taggedWord.get(classOf[CoreAnnotations.AnswerAnnotation])
          val tokenBegin = taggedWord.get(classOf[CoreAnnotations.CharacterOffsetBeginAnnotation])
          val tokenEnd = taggedWord.get(classOf[CoreAnnotations.CharacterOffsetEndAnnotation])

          (tokenType, currentType) match {
            case ("O", Some(b)) =>
              val namedEntity = new NamedEntity(jcas, begin, end)
              namedEntity.setValue(b)
              add(namedEntity)
              (begin, end, None)

            case (a, Some(b)) if (a != b) =>
              val namedEntity = new NamedEntity(jcas, begin, end)
              namedEntity.setValue(b)
              add(namedEntity)
              (begin, tokenEnd, Some(tokenType))

            case (a, None) if (a != "O") =>
              (tokenBegin, tokenEnd, Some(tokenType))

            case (a, Some(b)) if (a == b) =>
              (begin, tokenEnd, Some(tokenType))

            case ("O", None) =>
              (begin, end, currentType)
          }
        }
    }
  }

  def tokenToCoreLabel(token: Token): CoreLabel = {
    val word = new CoreLabel()
    word.setValue(token.getCoveredText)
    word.setOriginalText(token.getCoveredText)
    word.setWord(token.getCoveredText)
    word.setBeginPosition(token.getBegin)
    word.setEndPosition(token.getEnd)

    if (token.getPos != null) {
      word.setTag(token.getPos.getName)
    }

    word
  }
}
