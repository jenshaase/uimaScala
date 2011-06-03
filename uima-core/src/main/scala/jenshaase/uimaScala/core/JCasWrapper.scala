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

import org.apache.uima.jcas.JCas
import org.apache.uima.cas.text.AnnotationFS
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.jcas.cas.TOP
import scala.collection.JavaConversions._
import org.apache.uima.cas.FeatureStructure
import scala.collection.JavaConversions._
import collection.mutable.Buffer
import org.uimafit.util.{CasUtil, JCasUtil}

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
    // TODO: Use JCasUtil.selectPreceding when bug #83 of uimafit is solved
    val t = JCasUtil.getType(cas, typ)
    CasUtil.selectPreceding(cas.getCas, t, annotation, count).asInstanceOf[java.util.List[T]]
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
    // TODO: Use JCasUtil.selectFollowing when bug #83 of uimafit is solved
    val t = JCasUtil.getType(cas, typ)
    CasUtil.selectFollowing(cas.getCas, t, annotation, count).asInstanceOf[java.util.List[T]]
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