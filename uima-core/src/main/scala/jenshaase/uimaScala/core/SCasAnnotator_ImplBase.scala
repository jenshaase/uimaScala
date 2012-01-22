/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core

import configuration.Parameter
import java.io.File
import java.lang.reflect.Method
import java.net.URL
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.core.wrapper._
import org.apache.uima.analysis_component.AnalysisComponent
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase
import org.apache.uima.analysis_engine.AnalysisEngineDescription
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.resource.ResourceInitializationException
import org.apache.uima.resource.ResourceSpecifier
import org.apache.uima.UimaContext
import org.apache.uima.UIMAFramework
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.factory.ExternalResourceFactory
import scala.collection.mutable.ListBuffer
import xml.Node

/**
 * Scala Annotator.
 *
 * Loads the parameter when initialized
 *
 * @author Jens Haase <je.haase@googlemail.com>
 */
abstract class SCasAnnotator_ImplBase extends JCasAnnotator_ImplBase
    with Configurable
    with ConfigurationInitialization
    with ResourceInitialization
    with AsAnalysisEngine {

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
}