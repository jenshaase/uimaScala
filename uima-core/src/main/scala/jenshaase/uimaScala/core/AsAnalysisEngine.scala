/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import org.apache.uima.analysis_engine.AnalysisEngine

trait AsAnalysisEngine {
  def asAnalysisEngine: AnalysisEngine
}