/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.toolkit.configuration

import org.uimafit.descriptor.ConfigurationParameter
import java.util.Locale
import org.apache.uima.jcas.JCas
import jenshaase.uimaScala.core.configuration.parameter.OptionalLocaleParameter
import jenshaase.uimaScala.core.configuration.Configurable
import jenshaase.uimaScala.core.SCasAnnotator_ImplBase

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
trait LocaleConfig { this: SCasAnnotator_ImplBase ⇒

  object locale extends OptionalLocaleParameter(this)

  def getLocale(jcas: JCas): Locale = {
    val l = jcas.getDocumentLanguage()
    if (l != null && l != "x-unspecified") {
      return new Locale(l)
    }

    locale.is.getOrElse(Locale.getDefault())
  }
}