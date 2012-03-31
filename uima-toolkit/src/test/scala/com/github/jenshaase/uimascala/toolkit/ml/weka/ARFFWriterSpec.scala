/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import org.specs2.mutable.Specification
import java.io.File
import java.util.Date
import scala.io.Source

class ARFFWriterSpecs extends Specification {

  "A ARFF writer" should {
    "write a valid arff file" in {
      val target = new File("target/test/arff/test.arff")
      val test = new File("uima-toolkit/src/test/resources/arff/test.arff");

      val writer = new ARFFWriter(new File("target/test/arff/test.arff"), "test", List(
        NorminalAttribute("a1", Set("a", "b", "c"), "XXX"),
        IntAttribute("a2")), NorminalAttribute("out", Set("t", "f")))

      writer.write(Seq(
        NorminalFeature("a1", "a"),
        IntFeature("a2", 10)), NorminalFeature("out", "t"))

      writer.write(Seq(
        NorminalFeature("a1", "z"),
        IntFeature("a2", 20)), NorminalFeature("out", "f"))

      writer.write(Seq(
        IntFeature("a2", 20),
        NorminalFeature("a1", "c")), NorminalFeature("out", "f"))

      writer.finish

      getContent(test) must_== getContent(target)
    }
  }

  def getContent(file: File) =
    Source.fromFile(file).getLines().mkString("\n");
}