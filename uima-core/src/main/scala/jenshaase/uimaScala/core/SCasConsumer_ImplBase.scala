package jenshaase.uimaScala.core
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase
import configuration.ConfigurationInitialization
import jenshaase.uimaScala.core.resource.ResourceInitialization
import configuration.Configurable
import org.apache.uima.UimaContext
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.factory.ExternalResourceFactory

abstract class SCasConsumer_ImplBase extends JCasAnnotator_ImplBase
  with Configurable with ConfigurationInitialization with ResourceInitialization with Implicits {

  override def initialize(context: UimaContext) = {
    super.initialize(context)

    this.loadParameter(context)
    this.loadResources(context)
  }
  
  def asAnalysisEngine = {
    val aed = AnalysisEngineFactory.createPrimitiveDescription(this.niceClass, parameterKeyValues:_*)
    
    aed.setExternalResourceDependencies(resources.map(r => 
      ExternalResourceFactory.createExternalResourceDependency(r.name, r.className, !r.mandatory_?)).toArray)
    resources.foreach { r =>
      r.createBinding(aed)
    }

    AnalysisEngineFactory.createAggregate(aed)
  }
}