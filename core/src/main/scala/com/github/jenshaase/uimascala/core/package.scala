/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala

import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.cas.FSArray
import com.github.jenshaase.uimascala.core.wrapper._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.collection.CollectionReader

package object core {

  implicit def toScalaAnnotation(a: Annotation) = new AnnotationWrapper(a)

  implicit def toScalaCas(jcas: JCas) = new JCasWrapper(jcas)

  implicit def configBuilder[T <: Configurable](conf: T) = new ConfigurationBuilder(conf)

  @deprecated("See com.github.jenshaase.uimascala.core.SimplePipeline", "0.5.0")
  implicit def collectionReaderToPipeline(reader: SCasCollectionReader_ImplBase) = new SimplePipeline(reader.asCollectionReader)

  @deprecated("See com.github.jenshaase.uimascala.core.SimplePipeline", "0.5.0")
  implicit def collectionReaderToPipeline(reader: CollectionReader) = new SimplePipeline(reader)
}
