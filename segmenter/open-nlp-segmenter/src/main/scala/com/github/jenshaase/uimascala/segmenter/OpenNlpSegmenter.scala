package com.github.jenshaase.uimascala.segmenter

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource
import java.util.zip.GZIPInputStream
import scala.collection.JavaConversions._
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel

class OpenNlpSentenceSegmenterResource extends SharedResourceObject {
  private var model: SentenceDetectorME = _

  def load(data: DataResource) {
    val uri = data.getUri.toString

    if (new java.io.File(uri).exists) {
      model = new SentenceDetectorME(new SentenceModel(new java.io.File(uri)))
    } else {
      val resourceUri = if (uri.startsWith("/")) uri else "/" + uri
      val resource = this.getClass.getResource(resourceUri)

      val is = if (uri.endsWith(".gz")) {
        new GZIPInputStream(resource.openStream)
      } else {
        resource.openStream
      }

      model = new SentenceDetectorME(new SentenceModel(is))
    }
  }

  def getModel = model
}

class OpenNlpTokenSegmenterResource extends SharedResourceObject {
  private var model: TokenizerME = _

  def load(data: DataResource) {
    val uri = data.getUri.toString

    if (new java.io.File(uri).exists) {
      model = new TokenizerME(new TokenizerModel(new java.io.File(uri)))
    } else {
      val resourceUri = if (uri.startsWith("/")) uri else "/" + uri
      val resource = this.getClass.getResource(resourceUri)

      val is = if (uri.endsWith(".gz")) {
        new GZIPInputStream(resource.openStream)
      } else {
        resource.openStream
      }

      model = new TokenizerME(new TokenizerModel(is))
    }
  }

  def getModel = model
}

class OpenNlpSegmenter extends SCasAnnotator_ImplBase {

  object sentenceModel extends SharedResource[OpenNlpSentenceSegmenterResource]("")
  object tokenModel extends SharedResource[OpenNlpTokenSegmenterResource]("")

  def process(jcas: JCas) = {
    sentenceModel.resource.getModel.sentPosDetect(jcas.getDocumentText).foreach { span =>
      add(createSentence(jcas, span.getStart, span.getEnd))
    }

    jcas.select[Sentence].foreach { sentence =>
      tokenModel.resource.getModel.tokenizePos(sentence.getCoveredText).foreach { span =>
        add(createToken(jcas, span.getStart + sentence.getStart, span.getEnd + sentence.getStart))
      }
    }
  }

  protected def createToken(cas: JCas, begin: Int, end: Int) =
    new Token(cas, begin, end)

  protected def createSentence(cas: JCas, begin: Int, end: Int) =
    new Sentence(cas, begin, end)
}
