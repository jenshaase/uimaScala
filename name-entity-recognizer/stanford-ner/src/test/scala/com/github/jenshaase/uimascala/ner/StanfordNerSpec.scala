package com.github.jenshaase.uimascala.ner

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class StanfordNerSpec extends Specification {

  "The Stanford Parser" should {
    "add constituents" in {
      val parser: AnalysisEngine = new StanfordNer().
        config(
          _.model := SharedBinding[StanfordNerResource]("edu/stanford/nlp/models/ner/german.dewac_175m_600.crf.ser.gz")
        ).
        asAnalysisEngine

      val jcas = parser.newJCas()
      jcas.setDocumentText("Angela Merkel fliegt nach Berlin.")
      jcas.annotate[Sentence](0, 33)
      val t1 = jcas.annotate[Token](0, 6)
      val p1 = jcas.annotate[POS](0, 6)
      p1.setName("NE")
      t1.setPos(p1)

      val t2 = jcas.annotate[Token](7, 13)
      val p2 = jcas.annotate[POS](7, 13)
      p2.setName("NE")
      t2.setPos(p2)

      val t3 = jcas.annotate[Token](14, 20)
      val p3 = jcas.annotate[POS](14, 20)
      p3.setName("VVFIN")
      t3.setPos(p3)

      val t4 = jcas.annotate[Token](21, 25)
      val p4 = jcas.annotate[POS](21, 25)
      p4.setName("APPR")
      t4.setPos(p4)

      val t5 = jcas.annotate[Token](26, 32)
      val p5 = jcas.annotate[POS](26, 32)
      p5.setName("NE")
      t5.setPos(p5)

      val t6 = jcas.annotate[Token](32, 33)
      val p6 = jcas.annotate[POS](32, 33)
      p6.setName("$.")
      t6.setPos(p6)

      parser.process(jcas)

      val namedEntities = jcas.select[NamedEntity].toVector
      namedEntities.size must be equalTo(2)
      namedEntities(0).getCoveredText must be equalTo("Angela Merkel")
      namedEntities(0).getValue must be equalTo("I-PER")
      namedEntities(1).getCoveredText must be equalTo("Berlin")
      namedEntities(1).getValue must be equalTo("I-LOC")
    }
  }
}
