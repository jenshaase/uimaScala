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
import org.uimafit.util.{ CasUtil, JCasUtil }

/**
 * A JCas wrapper for implicity
 * @author Jens Haase <je.haase@googlemail.com>
 */
class JCasWrapper(cas: JCas) {

  /**
   * @see org.uimafit.uitl.JCasUtil#select
   */
  def select[T <: TOP](implicit mf: Manifest[T]): Iterable[T] = JCasUtil.select(cas, mf.erasure.asInstanceOf[Class[T]])

  /**
   * @see org.uimafit.uitl.JCasUtil#selectByIndex
   */
  def selectByIndex[T <: Annotation](index: Int)(implicit mf: Manifest[T]) =
    JCasUtil.selectByIndex(cas, mf.erasure.asInstanceOf[Class[T]], index)

  /**
   * @see org.uimafit.uitl.JCasUtil#selectCovered
   */
  def selectCovered[T <: Annotation](coveringAnnotation: Annotation)(implicit mf: Manifest[T]) =
    JCasUtil.selectCovered(cas, mf.erasure.asInstanceOf[Class[T]], coveringAnnotation)

  /**
   * @see org.uimafit.uitl.JCasUtil#isCovered
   */
  def isCovered[T <: Annotation](coveringAnnotation: Annotation)(implicit mf: Manifest[T]) =
    JCasUtil.isCovered(cas, coveringAnnotation, mf.erasure.asInstanceOf[Class[T]])

  /**
   * @see org.uimafit.uitl.JCasUtil#selectSingle
   */
  def selectSingle[T <: TOP](implicit mf: Manifest[T]) =
    JCasUtil.selectSingle(cas, mf.erasure.asInstanceOf[Class[T]])

  /**
   * @see org.uimafit.uitl.JCasUtil#selectPreceding
   */
  def selectPreceding[T <: Annotation](annotation: Annotation, count: Int = Int.MaxValue)(implicit mf: Manifest[T]): Buffer[T] = {
    JCasUtil.selectPreceding(cas, mf.erasure.asInstanceOf[Class[T]], annotation, count);
  }

  /**
   * @see org.uimafit.uitl.JCasUtil#selectFollowing
   */
  def selectFollowing[T <: Annotation](annotation: Annotation, count: Int = Int.MaxValue)(implicit mf: Manifest[T]): Buffer[T] = {
    JCasUtil.selectFollowing(cas, mf.erasure.asInstanceOf[Class[T]], annotation, count)
  }

  /**
   * @see org.uimafit.uitl.JCasUtil#exists
   */
  def exists[T <: TOP](implicit mf: Manifest[T]) =
    JCasUtil.exists(cas, mf.erasure.asInstanceOf[Class[T]])

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

  def selectRelative[T <: Annotation](annotaion: Annotation, index: Int)(implicit mf: Manifest[T]): Option[T] = {
    if (index > 0) {
      val foll = selectFollowing[T](annotaion, index)(mf)
      if (foll.size >= index) Some(foll(index - 1)) else None
    } else if (index < 0) {
      val prec = selectPreceding[T](annotaion, -index)(mf)
      if (prec.size >= -index) Some(prec(-index - 1)) else None
    } else {
      if (annotaion.isInstanceOf[T]) {
        Some(annotaion.asInstanceOf[T])
      } else {
        val covered = selectCovered[T](annotaion)(mf)
        if (covered.size > 0) Some(covered.get(0)) else None
      }
    }
  }
}