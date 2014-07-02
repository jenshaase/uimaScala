/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core

import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine
import org.apache.uima.jcas.JCas
import org.apache.uima.util.CasCreationUtils
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory
import scalaz._, Scalaz._
import scalaz.stream._

package object stream {

  type AnnotatorProcess = Process1[JCas, JCas]

  def annotate(f: (JCas => Any)): AnnotatorProcess =
    Process.await1[JCas].map { cas =>
      f(cas)
      cas
    }.repeat

  def annotate(a: AnalysisEngine): AnnotatorProcess =
    Process.await1[JCas].map { cas =>
      a.process(cas)
      cas
    }.repeat

  def annotate(a: AnalysisEngineDescription): AnnotatorProcess =
    annotate(createEngine(a))

  def initCas[I](f: ((I, JCas) => Any)): Process1[I, JCas] =
    Process.await1[I].map { something =>
      val cas = CasCreationUtils.createCas(
        TypeSystemDescriptionFactory.createTypeSystemDescription, null, null).getJCas

      f(something, cas)
      cas
    }.repeat

  def casFromText = initCas[String] { (str ,cas) =>
    cas.setDocumentText(str)
  }

  def extractCas[I](f: JCas => I): Process1[JCas, I] =
    Process.await1[JCas].map { cas =>
      f(cas)
    }.repeat
}
