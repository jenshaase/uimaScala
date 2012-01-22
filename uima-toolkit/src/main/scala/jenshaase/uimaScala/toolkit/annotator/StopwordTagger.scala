/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.annotator

import java.io.File
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.toolkit.types._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.jcas.JCas
import org.apache.uima.UimaContext
import org.uimafit.descriptor.ConfigurationParameter
import org.uimafit.factory.AnalysisEngineFactory
import scala.io.Source

class StopwordTagger extends SCasAnnotator_ImplBase {

  // TODO: Make Resource
  object stopwordFile extends Parameter[File](new File("src/main/resources/stopwords.txt")) {
    def loadStopwords: Set[String] =
      Source.fromFile(is).getLines.map(_.toLowerCase).toSet
  }

  protected var stopwords: Set[String] = _

  override def initialize(context: UimaContext) = {
    super.initialize(context)

    stopwords = stopwordFile.loadStopwords
  }

  def process(cas: JCas) = {
    cas.select[Token].foreach(t ⇒ {
      if (stopwords.contains(t.getCoveredText.toLowerCase)) {
        new Stopword(cas, t.getBegin, t.getEnd).addToIndexes
      }
    })
  }
}

class StopwordRemover extends StopwordTagger {

  override def process(cas: JCas) = {
    cas.select[Token].
      filter(t ⇒ stopwords.contains(t.getCoveredText.toLowerCase)).
      foreach(_.removeFromIndexes)
  }
}