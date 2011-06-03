package jenshaase.uimaScala.toolkit.configuration

import org.uimafit.descriptor.ConfigurationParameter
import java.util.Locale
import org.apache.uima.jcas.JCas

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
trait LocaleConfig {

  @ConfigurationParameter(name=Configuration.PARAM_LOCALE, mandatory=false)
  protected var locale: Locale = null
  
  def getLocale: Locale = {
    if (locale != null) {
      locale
    } else {
      Locale.getDefault
    }
  }
  
  def getLocale(jcas: JCas): Locale = {
    if (locale != null) {
      return locale
    }
    
    val l = jcas.getDocumentLanguage
    if (l != null) {
      new Locale(l)
    } else {
      Locale.getDefault
    }
  }
}