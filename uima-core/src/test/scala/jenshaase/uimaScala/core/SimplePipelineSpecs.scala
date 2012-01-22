/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core

import org.specs2.mutable.Specification
import org.apache.uima.jcas.JCas
import org.apache.uima.util.ProgressImpl
import org.apache.uima.jcas.tcas.Annotation

class SimplePipelineSpecs extends Specification {

  "SimplePipeline" should {
    "add one reader and one annotator" in {
      try {
        new PipelineDummyReader() ~> new PipelineAnnotatorA() run ()

        success
      } catch {
        case _ ⇒ failure
      }
    }

    "add one reader and two annotator" in {
      try {
        new PipelineDummyReader() ~>
          new PipelineAnnotatorA() ~>
          new PipelineAnnotatorB() run ()

        success
      } catch {
        case _ ⇒ failure
      }
    }
  }
}

class PipelineDummyReader extends SCasCollectionReader_ImplBase {
  val total = 2
  var i = total

  def getNext(cas: JCas) = {
    cas.setDocumentText("Doc" + i)
    i = i - 1
  }

  def getProgress = Array(new ProgressImpl(total - i, total, "test"))

  def hasNext = i > 0
}

class PipelineAnnotatorA extends SCasAnnotator_ImplBase {
  def process(cas: JCas) = {
    new Annotation(cas, 0, 1).addToIndexes
  }
}

class PipelineAnnotatorB extends SCasAnnotator_ImplBase {
  def process(cas: JCas) = {
    new Annotation(cas, 1, 2).addToIndexes
  }
}