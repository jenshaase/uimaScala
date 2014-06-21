/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core

import org.specs2.mutable.Specification
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.jcas.JCas
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.resource.Resource_ImplBase
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource

class SCasAnnotator_ImplBaseSpecs extends Specification {

  "SCasAnnotator_ImplBase" should {

    "initialize one string parameter in a Annotator" in {
      val d = new DummyAnnotator().config(
        _.stringParam := "dummy").asAnalysisEngine

      val cas = d.newJCas
      d.process(cas)

      cas.getDocumentText must be equalTo ("dummy")
    }

    "initalize two parameters in a Annotator" in {
      val d = new Dummy2Annotator().config(
        _.stringParam := "dummy",
        _.intParam := 1).asAnalysisEngine

      val cas = d.newJCas
      d.process(cas)

      cas.getDocumentText must be equalTo ("dummy1")
    }

    "initalize list parameter in a Annotator" in {
      val d = new Dummy3Annotator().config(
        _.listParam := List("2", "3")).asAnalysisEngine

      val cas = d.newJCas
      d.process(cas)

      cas.getDocumentText must be equalTo ("23")
    }

    "not require to set a optinal value" in {
      val d = new Dummy2Annotator().config(
        _.stringParam := "dummy").asAnalysisEngine

      val cas = d.newJCas
      d.process(cas)

      cas.getDocumentText must be equalTo ("dummy100")
    }

    "initialize a Annotator with a SharedResourceObject" in {
      val d = new ResourceDummyAnnotator().config(
        _.dict := SharedBinding[SharedDict](new java.io.File("/path/to/nowhere")),
        _.name := Binding[SharedName2]()).asAnalysisEngine
      val cas = d.newJCas
      d.process(cas)

      cas.getDocumentText() must be equalTo ("SharedDict|SharedName2")
    }
  }
}

class DummyAnnotator extends SCasAnnotator_ImplBase {

  object stringParam extends Parameter[String]("test")

  def process(cas: JCas) = {
    cas.setDocumentText(stringParam.is)
  }
}

class Dummy2Annotator extends SCasAnnotator_ImplBase {

  object stringParam extends Parameter[String]("test")
  object intParam extends Parameter[Int](100)

  def process(cas: JCas) = {
    cas.setDocumentText(stringParam.is + intParam.is)
  }
}

class Dummy3Annotator extends SCasAnnotator_ImplBase {

  object listParam extends Parameter[List[String]](List("a", "b"))

  def process(cas: JCas) = {
    cas.setDocumentText(listParam.is.foldLeft("")(_ + _))
  }
}

class SharedDict extends SharedResourceObject {
  def load(data: DataResource) = {}

  def name = "SharedDict"
}
class SharedName extends Resource_ImplBase { def name = "SharedName" }
class SharedName2 extends SharedName { override def name = "SharedName2" }

class ResourceDummyAnnotator extends SCasAnnotator_ImplBase {
  object dict extends SharedResource[SharedDict]("/path/to/nowhere")
  object name extends Resource[SharedName]

  def process(cas: JCas) = {
    cas.setDocumentText(dict.resource.name + "|" + name.resource.name);
  }
}
