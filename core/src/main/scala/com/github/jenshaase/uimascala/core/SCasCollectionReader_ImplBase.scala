/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core

import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.cas.CAS
import org.apache.uima.collection.CollectionReader_ImplBase
import org.apache.uima.jcas.JCas
import org.apache.uima.UimaContext
import org.apache.uima.fit.factory.CollectionReaderFactory
import org.apache.uima.fit.factory.ExternalResourceFactory

abstract class SCasCollectionReader_ImplBase extends CollectionReader_ImplBase
    with Configurable
    with ConfigurationInitialization
    with ResourceInitialization {

  override def initialize = {
    super.initialize

    loadParameter(getUimaContext)
    loadResources(getUimaContext)
    initialize(getUimaContext)
  }

  def initialize(context: UimaContext) = {}

  def asCollectionReader = {
    val aed = CollectionReaderFactory.createDescription(this.niceClass, parameterKeyValues: _*)

    aed.setExternalResourceDependencies(resources.map(r ⇒
      ExternalResourceFactory.createExternalResourceDependency(r.name, r.className, !r.mandatory_?, r.description)).toArray)
    resources.foreach { r ⇒
      r.createBinding(aed)
    }

    CollectionReaderFactory.createCollectionReader(aed)
  }

  def getNext(cas: CAS) = {
    getNext(cas.getJCas())
  }

  def getNext(cas: JCas)

  def close() = {}
}
