/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import org.specs2.mutable.Specification

class WekaConverterSpec extends Specification {

  "WekaConverter" should {
    "convert NorminalAttributes to Weka Attribute" in {
      val n = new NorminalAttribute("test", Set("a", "b", "c"), "X")
      val attr = WekaConverter.convertAttribute(n)

      attr.isNominal must beTrue
      attr.name must_== "test"
      attr.numValues must_== 4
    }

    "convert ClassAttribute to Weka Attribute" in {
      val n = ClassAttribute("test", Set("a", "b"))
      val attr = WekaConverter.convertAttribute(n)

      attr.isNominal must beTrue
      attr.name must_== "test"
      attr.numValues must_== 2
    }

    "convert NumericAttribute to Weka Attribute" in {
      val n = FloatAttribute("test")
      val attr = WekaConverter.convertAttribute(n)

      attr.isNumeric must beTrue
      attr.name must_== "test"
    }

    "convert DateAttribute to Weka Attribute" in {
      val n = DateAttribute("test")
      val attr = WekaConverter.convertAttribute(n)

      attr.isDate must beTrue
      attr.name must_== "test"
    }

    "create a Weka instance from a list of features" in {
      val meta = new Meta("test", List(
        NorminalAttribute("a", Set("a", "b")),
        FloatAttribute("b"),
        NorminalAttribute("c", Set("e", "f"), "X"),
        ClassAttribute("d", Set("t", "f"))))

      val features = List(NorminalFeature("a", "a"), FloatFeature("b", 10f), NorminalFeature("c", "z"))

      val inst = WekaConverter.featuresToInstance(features, meta)

      inst.classIsMissing must beTrue
      inst.stringValue(0) must_== "a"
      inst.value(1) must_== 10d
      inst.stringValue(2) must_== "X"
    }
  }
}