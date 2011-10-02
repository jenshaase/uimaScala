/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core.util

import org.apache.uima.jcas.JCas
import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.TypeSystemDescriptionFactory

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */

trait Helper {

  def newJCas: JCas = {
    CasCreationUtils.createCas(
      TypeSystemDescriptionFactory.createTypeSystemDescription, null, null).getJCas
  }
}