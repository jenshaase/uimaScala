/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala

import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.jcas.JCas
import jenshaase.uimaScala.core.wrapper._
import jenshaase.uimaScala.core.configuration._

package object core {

  implicit def toScalaAnnotation(a: Annotation) = new AnnotationWrapper(a)

  implicit def toScalaCas(jcas: JCas) = new JCasWrapper(jcas)

  implicit def configBuilder[T <: Configurable](conf: T) = new ConfigurationBuilder(conf)

  implicit def collectionReaderToPipeline(reader: SCasCollectionReader_ImplBase) = new SimplePipeline(reader)
}