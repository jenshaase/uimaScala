package jenshaase.uimaScala.core

import org.specs2.mutable.Specification
import jenshaase.uimaScala.core.configuration.parameter._
import org.apache.uima.jcas.JCas
import org.uimafit.factory.AnalysisEngineFactory
import org.apache.uima.resource.Resource_ImplBase
import jenshaase.uimaScala.core.resource.{Resource, SharedResource}
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource

class SCasAnnotator_ImplBaseSpecs extends Specification {

  "SCasAnnotator_ImplBase" should {

    "initialize one string parameter in a Annotator" in {
      val d = new DummyAnnotator().stringParam("dummy").asAnalysisEngine
      val cas = d.newJCas
      d.process(cas)

      cas.getDocumentText must be equalTo ("dummy")
    }

    "initalize two parameters in a Annotator" in {
      val d = new Dummy2Annotator().stringParam("dummy").intParam(Some(1)).asAnalysisEngine
      val cas = d.newJCas
      d.process(cas)

      cas.getDocumentText must be equalTo ("dummy1")
    }
    
    "not require to set a optinal value" in {
      val d = new Dummy2Annotator().stringParam("dummy").asAnalysisEngine
      val cas = d.newJCas
      d.process(cas)

      cas.getDocumentText must be equalTo ("dummy100")
    }
    
    "initialize a Annotator with a SharedResourceObject" in {
      val d = new ResourceDummyAnnotator().
    		  dict(classOf[SharedDict], "/path/to/nowhere").
    		  name(classOf[SharedName]).
    		  asAnalysisEngine
      val cas = d.newJCas
      d.process(cas)
      
      cas.getDocumentText() must be equalTo("SharedDict|SharedName")
    }
  }
}

class DummyAnnotator extends SCasAnnotator_ImplBase {

  object stringParam extends StringParameter(this)

  def process(cas: JCas) = {
    cas.setDocumentText(stringParam.is)
  }
}

class Dummy2Annotator extends SCasAnnotator_ImplBase {

  object stringParam extends StringParameter(this)
  object intParam extends OptionalIntParameter(this) {
    override def defaultValue = Some(100)
  }

  def process(cas: JCas) = {
    cas.setDocumentText(stringParam.is + intParam.is.getOrElse(""))
  }
}
class SharedDict extends SharedResourceObject {
  def load(data: DataResource) = {}
  
  def name = "SharedDict"
}
class SharedName extends Resource_ImplBase { def name = "SharedName" }
class ResourceDummyAnnotator extends SCasAnnotator_ImplBase {
  object dict extends SharedResource[SharedDict, ResourceDummyAnnotator](this)
  object name extends Resource[SharedName, ResourceDummyAnnotator](this)
  
  def process(cas: JCas) = {
    cas.setDocumentText(dict.resource.name +"|"+ name.resource.name);
  }
}