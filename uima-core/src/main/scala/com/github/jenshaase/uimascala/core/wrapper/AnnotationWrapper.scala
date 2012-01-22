/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core.wrapper

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
    var end = a.getEnd - 1

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
    case '\n'     ⇒ true
    case '\r'     ⇒ true
    case '\t'     ⇒ true
    case '\u200E' ⇒ true
    case '\u200F' ⇒ true
    case '\u2028' ⇒ true
    case '\u2029' ⇒ true
    case _        ⇒ Character.isWhitespace(c)
  }
}