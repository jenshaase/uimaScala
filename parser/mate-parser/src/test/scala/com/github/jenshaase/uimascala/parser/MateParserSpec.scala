package com.github.jenshaase.uimascala.parser

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.typesystem._
import com.github.jenshaase.uimascala.core.configuration._
import org.apache.uima.analysis_engine.AnalysisEngine
import org.specs2.mutable.Specification
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil

class MateParserSpec extends Specification {

  "The Mate Parser" should {
    "add dependencies" in {
      val parser: AnalysisEngine = new MateParser().
        config(
          _.model := SharedBinding[MateParserResource]("de/tudarmstadt/ukp/dkpro/core/matetools/lib/parser-de-tiger.model")
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

      val dependencies = jcas.select[Dependency].toVector
      dependencies(0).getCoveredText must be equalTo ("Wie")
      dependencies(0).getGovernor.getCoveredText must be equalTo ("alt")
      dependencies(0).getDependent.getCoveredText must be equalTo ("Wie")
      dependencies(0).getDependencyType must be equalTo ("MO")

      dependencies(1).getCoveredText must be equalTo ("alt")
      dependencies(1).getGovernor.getCoveredText must be equalTo ("bist")
      dependencies(1).getDependent.getCoveredText must be equalTo ("alt")
      dependencies(1).getDependencyType must be equalTo ("PD")

      dependencies(2).getCoveredText must be equalTo ("bist")
      dependencies(2).getGovernor.getCoveredText must be equalTo ("bist")
      dependencies(2).getDependent.getCoveredText must be equalTo ("bist")
      dependencies(2).getDependencyType must be equalTo ("--")

      dependencies(3).getCoveredText must be equalTo ("du")
      dependencies(3).getGovernor.getCoveredText must be equalTo ("bist")
      dependencies(3).getDependent.getCoveredText must be equalTo ("du")
      dependencies(3).getDependencyType must be equalTo ("SB")

      dependencies(4).getCoveredText must be equalTo ("?")
      dependencies(4).getGovernor.getCoveredText must be equalTo ("du")
      dependencies(4).getDependent.getCoveredText must be equalTo ("?")
      dependencies(4).getDependencyType must be equalTo ("--")
    }
  }
}
