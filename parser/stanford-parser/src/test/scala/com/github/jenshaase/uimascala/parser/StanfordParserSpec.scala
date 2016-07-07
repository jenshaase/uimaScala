package com.github.jenshaase.uimascala.parser

import java.util.Locale
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class StanfordParserSpec extends Specification {

  "The Stanford Parser" should {
    "add constituents" in {
      val parser: AnalysisEngine = new StanfordParser().
        config(
          _.model := SharedBinding[StanfordParserGrammerResource]("edu/stanford/nlp/models/srparser/germanSR.ser.gz")
        ).
        asAnalysisEngine

      val jcas = parser.newJCas()
      jcas.setDocumentText("Wie alt bist du?")
      jcas.annotate[Sentence](0, 16)
      val t1 = jcas.annotate[Token](0, 3)
      val p1 = jcas.annotate[POS](0, 3)
      p1.setName("PWAV")
      t1.setPos(p1)

      val t2 = jcas.annotate[Token](4, 7)
      val p2 = jcas.annotate[POS](4, 7)
      p2.setName("ADJD")
      t2.setPos(p2)

      val t3 = jcas.annotate[Token](8, 12)
      val p3 = jcas.annotate[POS](8, 12)
      p3.setName("VAFIN")
      t3.setPos(p3)

      val t4 = jcas.annotate[Token](13, 15)
      val p4 = jcas.annotate[POS](13, 15)
      p4.setName("PPER")
      t4.setPos(p4)

      val t5 = jcas.annotate[Token](15, 16)
      val p5 = jcas.annotate[POS](15, 16)
      p5.setName("$.")
      t5.setPos(p5)

      parser.process(jcas)

      val constituents = jcas.select[Constituent].toVector
      constituents(0).getBegin must be equalTo (0)
      constituents(0).getEnd must be equalTo (16)
      constituents(0).getConstituentType must be equalTo ("S")
      constituents(0).getChildren.size must be equalTo (4)
      constituents(0).getParent must be equalTo(constituents(1))

      constituents(1).getBegin must be equalTo (0)
      constituents(1).getEnd must be equalTo (16)
      constituents(1).getConstituentType must be equalTo ("ROOT")
      constituents(1).getChildren.size must be equalTo (1)
      constituents(1).getParent must beNull

      constituents(2).getBegin must be equalTo (0)
      constituents(2).getEnd must be equalTo (7)
      constituents(2).getConstituentType must be equalTo ("AP")
      constituents(2).getChildren.size must be equalTo (2)
      constituents(2).getParent must be equalTo(constituents(0))

      val tokens = jcas.select[Token].toVector
      tokens(0).getParent must be equalTo(constituents(2))
      tokens(1).getParent must be equalTo(constituents(2))
      tokens(2).getParent must be equalTo(constituents(0))
      tokens(3).getParent must be equalTo(constituents(0))
      tokens(4).getParent must be equalTo(constituents(0))
    }
  }
}
