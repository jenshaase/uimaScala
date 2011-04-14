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

import org.apache.uima.jcas.tcas.Annotation

/**
 * A Uima Annotation wrapper for implicity.
 * @author Jens Haase <je.haase@googlemail.com>
 */
class AnnotationWrapper(a: Annotation) {

  /**
   * Remove whitespace before and after the annotation
   * by increasing/decreasing the begin/end value
   */
  def trim: Annotation = {
    var begin = a.getBegin
    var end = a.getEnd -1
    
    val data = a.getCAS.getDocumentText
    
    while (begin < (data.length - 1) && trimChar(data.charAt(begin)))
      begin += 1
    
    while (end > 0 && trimChar(data.charAt(end)))
      end -= 1
    
    end += 1
    a.setBegin(begin)
    a.setEnd(end)
    
    a
  }

  /**
   * Add annotation to index if the covering text
   * of the annotation is not empty
   */
  def addToIndexIfNotEmpty = if (!isEmpty) a.addToIndexes

  /**
   * Checks if the covering text of the annotation
   * is empty
   */
  def isEmpty = a.getBegin >= a.getEnd
  
  protected def trimChar(c: Char): Boolean = c match {
    case '\n' => true
    case '\r' => true
    case '\t' => true
    case '\u200E' => true
    case '\u200F' => true
    case '\u2028' => true
    case '\u2029' => true
    case _ => Character.isWhitespace(c)
  }
}