/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.toolkit.configuration

import java.util.Locale
import jenshaase.uimaScala.core.configuration._
import jenshaase.uimaScala.core.SCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas
import org.uimafit.descriptor.ConfigurationParameter

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