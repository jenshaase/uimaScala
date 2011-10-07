/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import configuration._
import org.apache.uima.flow.FlowControllerContext
import org.apache.uima.flow.JCasFlowController_ImplBase
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.factory.FlowControllerFactory

abstract class SCasFlowController_ImplBase extends JCasFlowController_ImplBase
    with Configurable
    with ConfigurationInitialization
    with ResourceInitialization {

  override def initialize(context: FlowControllerContext) = {
    super.initialize(context)

    this.loadParameter(context)
  }

  def asAnalysisEngine = {
    FlowControllerFactory.createFlowControllerDescription(this.niceClass, this.parameterKeyValues: _*)
  }
}