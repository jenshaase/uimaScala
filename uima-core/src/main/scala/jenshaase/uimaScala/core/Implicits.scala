/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.jcas.JCas
import jenshaase.uimaScala.core.wrapper._

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
trait Implicits {

  implicit def toScalaAnnotation(a: Annotation) = new AnnotationWrapper(a)

  implicit def toScalaCas(jcas: JCas) = new JCasWrapper(jcas)
}

object Implicits extends Implicits