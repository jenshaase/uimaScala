/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.utils

import org.specs2.mutable.Specification

class FreqDistSpecs extends Specification {

  "A frequency distribution" should {
    "count frequencies" in {
      val f = FreqDist("hello", "world", "hello")

      f.get("hello") must beSome.which(_ mustEqual 2)
      f.get("world") must beSome.which(_ mustEqual 1)
    }

    "increment values" in {
      val f = FreqDist("hello", "world") inc "hello"

      f.get("hello") must beSome.which(_ mustEqual 2)
      f.get("world") must beSome.which(_ mustEqual 1)
    }

    "return hapaxes" in {
      val f = FreqDist("hello", "world", "hello", "bla")

      f.hapaxes must contain("world", "bla").only
    }

    "return a sorted list" in {
      val f = FreqDist("hello", "world", "hello")

      f.sorted must contain(("hello", 2), ("world", 1)).only.inOrder
    }
  }
}