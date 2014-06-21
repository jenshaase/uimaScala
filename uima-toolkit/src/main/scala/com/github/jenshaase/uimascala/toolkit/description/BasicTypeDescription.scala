/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.description

import com.github.jenshaase.uimascala.core.description._
//import UimaTyp._

@TypeSystemDescription
object BasicTypeDescription {

  val DocumentAnnotation = Annotation {
    val name = Feature[String]
    val source = Feature[String]
  }

  val Token = Annotation {
    val POS = Feature[String]
    val lemma = Feature[String]
    val stem = Feature[String]
  }

  val Sentence = Annotation {}

  val Stopword = Annotation {}
}
