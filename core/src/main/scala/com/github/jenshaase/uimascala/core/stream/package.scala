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
import fs2._

package object stream {

  type AnnotatorProcess[F[_]] = Pipe[F, JCas, JCas]

  def annotate[F[_]](f: (JCas => Any)): AnnotatorProcess[F] =
    _.map { cas =>
      f(cas)
      cas
    }

  def annotate[F[_]](a: AnalysisEngine): AnnotatorProcess[F] =
    _.map { cas =>
      a.process(cas)
      cas
    }

  def annotate[F[_]](a: AnalysisEngineDescription): AnnotatorProcess[F] =
    annotate(createEngine(a))

  def annotate[F[_]](a: AsAnalysisEngine): AnnotatorProcess[F] =
    annotate(a.asAnalysisEngine)

  def initCas[F[_], I](f: ((I, JCas) => Any)): Pipe[F, I, JCas] =
    _.map { something =>
      val cas = CasCreationUtils.createCas(
        TypeSystemDescriptionFactory.createTypeSystemDescription, null, null).getJCas

      f(something, cas)
      cas
    }

  def casFromText[F[_]] = initCas[F, String] { (str ,cas) =>
    cas.setDocumentText(str)
  }

  def extractCas[F[_], I](f: JCas => I): Pipe[F, JCas, I] =
    _.map(f)
}
