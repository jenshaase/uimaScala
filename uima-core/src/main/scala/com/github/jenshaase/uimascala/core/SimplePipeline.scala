/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core

class SimplePipeline(reader: SCasCollectionReader_ImplBase) {

  private var descs: Seq[AsAnalysisEngine] = Seq.empty

  def ~>(in: AsAnalysisEngine) = {
    descs = descs :+ in
    this
  }

  def run() = {
    org.uimafit.pipeline.SimplePipeline.runPipeline(reader.asCollectionReader, descs.map(_.asAnalysisEngine): _*)
  }
}

object SimplePipeline {

  def apply(reader: SCasCollectionReader_ImplBase) =
    new SimplePipeline(reader)
}