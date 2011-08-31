/*
 * Copyright (C) 2011 by Jens Haase <je.haase@googlemail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

/**
 * Scala Annotator.
 *
 * Loads the parameter when initialized
 *
 * @author Jens Haase <je.haase@googlemail.com>
 */
abstract class SCasAnnotator_ImplBase extends JCasAnnotator_ImplBase
  with Configurable with ConfigurationInitialization with ResourceInitialization with Implicits {

  override def initialize(context: UimaContext) = {
    super.initialize(context)

    this.loadParameter(context)
    this.loadResources(context)
  }

  /**
   * Creates a analysis engine from an Annotator instance
   */
  def asAnalysisEngine = {
    val aed = AnalysisEngineFactory.createPrimitiveDescription(this.niceClass, parameterKeyValues:_*)
    
    aed.setExternalResourceDependencies(resources.map(r => 
      ExternalResourceFactory.createExternalResourceDependency(r.name, r.className, !r.mandatory_?)).toArray)
    resources.foreach { r =>
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