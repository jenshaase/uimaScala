/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core

import configuration._
import org.apache.uima.analysis_component.JCasMultiplier_ImplBase
import org.apache.uima.UimaContext
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.factory.ExternalResourceFactory

abstract class SCasMultiplier_ImplBase extends JCasMultiplier_ImplBase
    with Configurable
    with ConfigurationInitialization
    with ResourceInitialization {

  override def initialize(context: UimaContext) = {
    super.initialize(context)

    this.loadParameter(context)
    this.loadResources(context)
  }

  def asAnalysisEngine = {
    val aed = AnalysisEngineFactory.createPrimitiveDescription(this.niceClass, parameterKeyValues: _*)

    aed.setExternalResourceDependencies(resources.map(r ⇒
      ExternalResourceFactory.createExternalResourceDependency(r.name, r.className, !r.mandatory_?)).toArray)
    resources.foreach { r ⇒
      r.createBinding(aed)
    }

    AnalysisEngineFactory.createAggregate(aed)
  }
}