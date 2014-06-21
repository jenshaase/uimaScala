/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.configuration

import java.util.Locale
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.core.SCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas
import org.apache.uima.fit.descriptor.ConfigurationParameter

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
trait LocaleConfig { this: SCasAnnotator_ImplBase ⇒

  object locale extends Parameter[Locale](Locale.getDefault)

  def getLocale(jcas: JCas): Locale = {
    val l = jcas.getDocumentLanguage()
    if (l != null && l != "x-unspecified") {
      return new Locale(l)
    }

    locale.is
  }
}
