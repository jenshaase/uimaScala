/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import org.specs2.mutable.Specification

class MetaSpec extends Specification {

  "A Meta object" should {
    "return the class attribute" in {
      val m = new Meta("test", Seq(
        NorminalAttribute("t", Set("a", "b"), "X"),
        ClassAttribute("c", Set("t", "f"))))

      m.classAttribute must_== ClassAttribute("c", Set("t", "f"))
    }

    "return the class index" in {
      val m = new Meta("test", Seq(
        NorminalAttribute("t", Set("a", "b"), "X"),
        ClassAttribute("c", Set("t", "f"))))

      m.classIndex must_== Some(1)
    }

    "must be convertable to xml" in {
      val m = new Meta("test", Seq(
        NorminalAttribute("t", Set("a", "b"), "X"),
        FloatAttribute("f"),
        ClassAttribute("c", Set("t", "f"))))

      m.toXml must ==/(
        <wekameta>
          <relation>test</relation>
          <attributes>
            <attribute type="norminal">
              <name>t</name>
              <norminals>
                <norminal>a</norminal>
                <norminal>b</norminal>
              </norminals>
              <fallback>X</fallback>
            </attribute>
            <attribute type="float">
              <name>f</name>
            </attribute>
            <attribute type="class">
              <name>c</name>
              <norminals>
                <norminal>t</norminal>
                <norminal>f</norminal>
              </norminals>
            </attribute>
          </attributes>
        </wekameta>)
    }

    "be stored on disk" in {
      val m1 = new Meta("test", Seq(
        NorminalAttribute("t", Set("a", "b"), "X"),
        FloatAttribute("f"),
        ClassAttribute("c", Set("t", "f"))))

      Meta.save("target/test_meta.xml", m1)
      val m2 = Meta.load("target/test_meta.xml")

      m2.attributes must_== m1.attributes
      m2.relation must_== m2.relation
    }
  }

}