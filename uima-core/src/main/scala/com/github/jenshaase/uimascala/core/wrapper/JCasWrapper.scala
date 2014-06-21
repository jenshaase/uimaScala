/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.wrapper

import org.apache.uima.jcas.JCas
import org.apache.uima.cas.text.AnnotationFS
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.jcas.cas.TOP
import scala.collection.JavaConversions._
import org.apache.uima.cas.FeatureStructure
import scala.collection.JavaConversions._
import collection.mutable.Buffer
import org.apache.uima.fit.util.{ CasUtil, JCasUtil }
import scala.reflect.ClassTag

/**
 * A JCas wrapper for implicity
 * @author Jens Haase <je.haase@googlemail.com>
 */
class JCasWrapper(cas: JCas) {

  def create[T <: TOP](f: (T => Unit)*)(implicit cf: ClassTag[T]): T = {
    val constructor = cf.runtimeClass.getConstructor(classOf[JCas])
    val obj = constructor.newInstance(cas).asInstanceOf[T]
    f.foreach { f => f(obj) }
    obj.addToIndexes()
    obj
  }

  def annotate[T <: Annotation](begin: Int, end: Int)(f: (T => Unit)*)(implicit cf: ClassTag[T]): T = {
    val constructor = cf.runtimeClass.getConstructor(classOf[JCas])
    val obj = constructor.newInstance(cas).asInstanceOf[T]
    obj.setBegin(begin)
    obj.setEnd(end)
    f.foreach { f => f(obj) }
    obj.addToIndexes()
    obj
  }

  /**
   * @see org.apache.uima.fit.uitl.JCasUtil#select
   */
  def select[T <: TOP](implicit cf: ClassTag[T]): Iterable[T] =
    JCasUtil.select(cas, cf.runtimeClass.asInstanceOf[Class[T]])

  /**
   * @see org.apache.uima.fit.uitl.JCasUtil#selectByIndex
   */
  def selectByIndex[T <: Annotation](index: Int)(implicit cf: ClassTag[T]) =
    JCasUtil.selectByIndex(cas, cf.runtimeClass.asInstanceOf[Class[T]], index)

  /**
   * @see org.apache.uima.fit.uitl.JCasUtil#selectCovered
   */
  def selectCovered[T <: Annotation](coveringAnnotation: Annotation)(implicit cf: ClassTag[T]) =
    JCasUtil.selectCovered(cas, cf.runtimeClass.asInstanceOf[Class[T]], coveringAnnotation)

  /**
   * @see org.apache.uima.fit.uitl.JCasUtil#selectSingle
   */
  def selectSingle[T <: TOP](implicit cf: ClassTag[T]) =
    JCasUtil.selectSingle(cas, cf.runtimeClass.asInstanceOf[Class[T]])

  /**
   * @see org.apache.uima.fit.uitl.JCasUtil#selectPreceding
   */
  def selectPreceding[T <: Annotation](annotation: Annotation, count: Int = Int.MaxValue)(implicit cf: ClassTag[T]): Buffer[T] = {
    JCasUtil.selectPreceding(cas, cf.runtimeClass.asInstanceOf[Class[T]], annotation, count);
  }

  /**
   * @see org.apache.uima.fit.uitl.JCasUtil#selectFollowing
   */
  def selectFollowing[T <: Annotation](annotation: Annotation, count: Int = Int.MaxValue)(implicit cf: ClassTag[T]): Buffer[T] = {
    JCasUtil.selectFollowing(cas, cf.runtimeClass.asInstanceOf[Class[T]], annotation, count)
  }

  /**
   * @see org.apache.uima.fit.uitl.JCasUtil#exists
   */
  def exists[T <: TOP](implicit ct: ClassTag[T]) =
    JCasUtil.exists(cas, ct.runtimeClass.asInstanceOf[Class[T]])

  /**
   * @see org.apache.uima.fit.uitl.JCasUtil#getView
   */
  def getView(name: String, fallback: JCas) =
    JCasUtil.getView(cas, name, fallback)

  /**
   * @see org.apache.uima.fit.uitl.JCasUtil#getView
   */
  def getView(name: String, create: Boolean) =
    JCasUtil.getView(cas, name, create)
}
