package com.github.jenshaase.uimascala.pos

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem.{Token, POS}
import org.apache.uima.jcas.JCas
import cmu.arktweetnlp.Twokenize
import scala.collection.JavaConversions._
import cmu.arktweetnlp.impl.Model
import cmu.arktweetnlp.impl.features.FeatureExtractor
import org.apache.uima.UimaContext;
import cmu.arktweetnlp.impl.ModelSentence
import cmu.arktweetnlp.impl.Sentence

class ArkTweetPosTagger extends SCasAnnotator_ImplBase {

  object modelLocation extends Parameter[String]("")

  private var model: Model = _
  private var featureExtractor: FeatureExtractor = _

  override def initialize(context: UimaContext) {
    super.initialize(context)

    model = Model.loadModelFromText(modelLocation.is)
    featureExtractor = new FeatureExtractor(model, false);
  }

  def process(jcas: JCas) = {
    val tokens = jcas.select[Token].toVector

    val sentence = new Sentence()
    sentence.tokens = tokens.map(_.getCoveredText)
    val ms = new ModelSentence(sentence.T())
    featureExtractor.computeFeatures(sentence, ms)
    model.greedyDecode(ms, false)

    tokens.zipWithIndex.foreach { case (token, idx) =>
      val tag = model.labelVocab.name( ms.labels(idx) );

      val pos = new POS(jcas, token.getBegin, token.getEnd)
      pos.setName(tag)
      add(pos)

      token.setPos(pos)
    }
  }

  def createToken(cas: JCas, begin: Int, end: Int) =
    new Token(cas, begin, end)
}
