package jenshaase.uimaScala.core

import org.apache.uima.flow.JCasFlowController_ImplBase
import jenshaase.uimaScala.core.resource.ResourceInitialization
import configuration.Configurable
import configuration.ConfigurationInitialization
import org.apache.uima.flow.FlowControllerContext
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.factory.FlowControllerFactory

abstract class SCasFlowController_ImplBase extends JCasFlowController_ImplBase
  with Configurable with ConfigurationInitialization with ResourceInitialization with Implicits {

  override def initialize(context: FlowControllerContext) = {
    super.initialize(context)

    this.loadParameter(context)
  }
  
  def asAnalysisEngine = {
    FlowControllerFactory.createFlowControllerDescription(this.niceClass, this.parameterKeyValues:_*)
  }
}