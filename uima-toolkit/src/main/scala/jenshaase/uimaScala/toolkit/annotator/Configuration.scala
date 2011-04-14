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

package jenshaase.uimaScala.toolkit.annotator

import org.uimafit.descriptor.ConfigurationParameter
import org.uimafit.descriptor.ConfigurationParameter._
import java.util.Locale
import org.apache.uima.jcas.JCas

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */

object Configuration {
  final val PARAM_LOCALE = "Locale"
}

trait LocaleConfig {
  
  @ConfigurationParameter(name=Configuration.PARAM_LOCALE, mandatory=false)
  protected var locale: String = null
  
  def getLocale: Locale = {
    if (locale != null) {
      new Locale(locale)
    } else {
      Locale.getDefault
    }
  }
  
  def getLocale(jcas: JCas): Locale = {
    if (locale != null) {
      return new Locale(locale)
    }
    
    val l = jcas.getDocumentLanguage
    if (l != null) {
      new Locale(l)
    } else {
      Locale.getDefault
    }
  }
}