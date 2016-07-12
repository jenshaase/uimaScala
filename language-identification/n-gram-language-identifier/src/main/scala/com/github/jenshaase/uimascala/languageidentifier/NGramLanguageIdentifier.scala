package com.github.jenshaase.uimascala.languageidentifier

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import scala.collection.JavaConversions._
import com.optimaize.langdetect.text.CommonTextObjectFactories
import com.optimaize.langdetect.ngram.NgramExtractors
import com.optimaize.langdetect.profiles._
import com.optimaize.langdetect._

class NGramLanguageIdentifier extends SCasAnnotator_ImplBase {

  object shortText extends Parameter[Boolean](false)

  lazy val languageDetector = {
    val languageProfiles = new LanguageProfileReader().readAllBuiltIn()
    LanguageDetectorBuilder.create(NgramExtractors.standard())
      .withProfiles(languageProfiles)
      .build()
  }

  def process(jcas: JCas) = {
    val textObjectFactory = 
      if (shortText.is) {
        CommonTextObjectFactories.forDetectingOnLargeText()
      } else {
        CommonTextObjectFactories.forDetectingShortCleanText()
      }

    val text = textObjectFactory.forText(jcas.getDocumentText);
    val lang = languageDetector.detect(text)
    if (lang.isPresent()) {
      jcas.setDocumentLanguage(lang.get().toString)
    }
  }
}
