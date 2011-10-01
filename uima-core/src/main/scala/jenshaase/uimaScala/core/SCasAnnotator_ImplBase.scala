/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.UimaContext
import java.lang.reflect.Method
import configuration.Parameter
import org.apache.uima.analysis_component.AnalysisComponent
import scala.collection.mutable.ListBuffer
import org.apache.uima.resource.ResourceInitializationException
import org.uimafit.factory.AnalysisEngineFactory
import jenshaase.uimaScala.core.configuration.{ Configurable, ConfigurationInitialization }
import jenshaase.uimaScala.core.resource.ResourceInitialization
import jenshaase.uimaScala.core.wrapper._
import org.apache.uima.jcas.JCas
import org.uimafit.factory.ExternalResourceFactory
import java.net.URL
import java.io.File
import jenshaase.uimaScala.core.resource.Resource
import org.apache.uima.resource.ResourceSpecifier
import jenshaase.uimaScala.core.resource.TypedSharedResource
import org.apache.uima.UIMAFramework
import org.apache.uima.analysis_engine.AnalysisEngineDescription
import xml.Node

/**
 * Scala Annotator.
 *
 * Loads the parameter when initialized
 *
 * @author Jens Haase <je.haase@googlemail.com>
 */
abstract class SCasAnnotator_ImplBase extends JCasAnnotator_ImplBase
  with Configurable with ConfigurationInitialization with ResourceInitialization with Implicits with XmlDescriptor {

  override def initialize(context: UimaContext) = {
    super.initialize(context)

    this.loadParameter(context)
    this.loadResources(context)
  }

  /**
   * Creates a analysis engine from an Annotator instance
   */
  def asAnalysisEngine = {
    val aed = AnalysisEngineFactory.createPrimitiveDescription(this.niceClass, parameterKeyValues: _*)

    aed.setExternalResourceDependencies(resources.map(r ⇒
      ExternalResourceFactory.createExternalResourceDependency(r.name, r.className, !r.mandatory_?)).toArray)
    resources.foreach { r ⇒
      r.createBinding(aed)
    }

    AnalysisEngineFactory.createAggregate(aed)
  }

  /**
   * Adds an annotation to the index
   * if the annotation is not empty
   */
  def addIfNotEmpty[T <: Annotation](a: T): T = if (!a.isEmpty) {
    add(a)
  } else {
    a
  }

  /**
   * Adds a annotation to the index
   */
  def add[T <: Annotation](a: T): T = {
    a.addToIndexes
    a
  }

  def toXml: Node =
    <todo></todo>
}