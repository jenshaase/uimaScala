/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.wrapper

import org.apache.uima.jcas.JCas
import org.apache.uima.cas.text.AnnotationFS
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.jcas.cas.TOP
import scala.collection.JavaConversions._
import org.apache.uima.cas.FeatureStructure
import scala.collection.JavaConversions._
import collection.mutable.Buffer
import org.uimafit.util.{ CasUtil, JCasUtil }

/**
 * A JCas wrapper for implicity
 * @author Jens Haase <je.haase@googlemail.com>
 */
class JCasWrapper(cas: JCas) {

  /**
   * @see org.uimafit.uitl.JCasUtil#select
   */
  def select[T <: TOP](typ: Class[T]): Iterable[T] = JCasUtil.select(cas, typ)

  /**
   * @see org.uimafit.uitl.JCasUtil#selectByIndex
   */
  def selectByIndex[T <: Annotation](typ: Class[T], index: Int) =
    JCasUtil.selectByIndex(cas, typ, index)

  /**
   * @see org.uimafit.uitl.JCasUtil#selectCovered
   */
  def selectCovered[T <: Annotation](typ: Class[T], coveringAnnotation: Annotation) =
    JCasUtil.selectCovered(cas, typ, coveringAnnotation)

  /**
   * @see org.uimafit.uitl.JCasUtil#isCovered
   */
  def isCovered[T <: Annotation](coveringAnnotation: Annotation, typ: Class[T]) =
    JCasUtil.isCovered(cas, coveringAnnotation, typ)

  /**
   * @see org.uimafit.uitl.JCasUtil#selectSingle
   */
  def selectSingle[T <: TOP](typ: Class[T]) =
    JCasUtil.selectSingle(cas, typ)

  /**
   * @see org.uimafit.uitl.JCasUtil#selectPreceding
   */
  def selectPreceding[T <: Annotation](typ: Class[T], annotation: Annotation): Buffer[T] =
    selectPreceding(typ, annotation, Int.MaxValue)

  /**
   * @see org.uimafit.uitl.JCasUtil#selectPreceding
   */
  def selectPreceding[T <: Annotation](typ: Class[T], annotation: Annotation, count: Int): Buffer[T] = {
    JCasUtil.selectPreceding(cas, typ, annotation, count);
  }

  /**
   * @see org.uimafit.uitl.JCasUtil#selectFollowing
   */
  def selectFollowing[T <: Annotation](typ: Class[T], annotation: Annotation): Buffer[T] =
    selectFollowing(typ, annotation, Int.MaxValue)

  /**
   * @see org.uimafit.uitl.JCasUtil#selectFollowing
   */
  def selectFollowing[T <: Annotation](typ: Class[T], annotation: Annotation, count: Int): Buffer[T] = {
    JCasUtil.selectFollowing(cas, typ, annotation, count)
  }

  /**
   * @see org.uimafit.uitl.JCasUtil#exists
   */
  def exists[T <: TOP](typ: Class[T]) =
    JCasUtil.exists(cas, typ)

  /**
   * @see org.uimafit.uitl.JCasUtil#getView
   */
  def getView(name: String, fallback: JCas) =
    JCasUtil.getView(cas, name, fallback)

  /**
   * @see org.uimafit.uitl.JCasUtil#getView
   */
  def getView(name: String, create: Boolean) =
    JCasUtil.getView(cas, name, create)
}