package com.github.jenshaase.uimascala.pos

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class ArkTweetPosTaggerSpec extends Specification {

  "Ark Tweet Pos Tagger" should {
    "add POS tags" in {
      val modelPath = new java.io.File(getClass.getResource("/model.20120919").toURI).getAbsolutePath
      val tagger: AnalysisEngine = new ArkTweetPosTagger().
        config(
          _.modelLocation := modelPath
        ).
        asAnalysisEngine

      val jcas = tagger.newJCas()
      jcas.setDocumentText("RT @DjBlack_Pearl: wat muhfuckaz wearin 4 the lingerie party?????")
      jcas.annotate[Token](0, 2)
      jcas.annotate[Token](3, 17)
      jcas.annotate[Token](17, 18)
      jcas.annotate[Token](19, 22)
      jcas.annotate[Token](23, 32)
      jcas.annotate[Token](33, 39)
      jcas.annotate[Token](40, 41)
      jcas.annotate[Token](42, 45)
      jcas.annotate[Token](46, 54)
      jcas.annotate[Token](55, 60)
      jcas.annotate[Token](60, 65)
      tagger.process(jcas)

      jcas.select[POS].size must be equalTo(11)
      jcas.selectByIndex[POS](0).getCoveredText must be equalTo ("RT")
      jcas.selectByIndex[POS](0).getName must be equalTo ("~")
      jcas.selectByIndex[POS](1).getCoveredText must be equalTo ("@DjBlack_Pearl")
      jcas.selectByIndex[POS](1).getName must be equalTo ("@")
      jcas.selectByIndex[POS](2).getCoveredText must be equalTo (":")
      jcas.selectByIndex[POS](2).getName must be equalTo ("~")
      jcas.selectByIndex[POS](3).getCoveredText must be equalTo ("wat")
      jcas.selectByIndex[POS](3).getName must be equalTo ("O")
      jcas.selectByIndex[POS](4).getCoveredText must be equalTo ("muhfuckaz")
      jcas.selectByIndex[POS](4).getName must be equalTo ("N")
      jcas.selectByIndex[POS](5).getCoveredText must be equalTo ("wearin")
      jcas.selectByIndex[POS](5).getName must be equalTo ("V")
      jcas.selectByIndex[POS](6).getCoveredText must be equalTo ("4")
      jcas.selectByIndex[POS](6).getName must be equalTo ("P")
      jcas.selectByIndex[POS](7).getCoveredText must be equalTo ("the")
      jcas.selectByIndex[POS](7).getName must be equalTo ("D")
      jcas.selectByIndex[POS](8).getCoveredText must be equalTo ("lingerie")
      jcas.selectByIndex[POS](8).getName must be equalTo ("N")
      jcas.selectByIndex[POS](9).getCoveredText must be equalTo ("party")
      jcas.selectByIndex[POS](9).getName must be equalTo ("N")
      jcas.selectByIndex[POS](10).getCoveredText must be equalTo ("?????")
      jcas.selectByIndex[POS](10).getName must be equalTo (",")
    }
  }
}
