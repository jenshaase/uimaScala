/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import org.specs2._
import matcher._

class ConverterSpec extends Specification with DataTables {
  import CastFactory._

  // format: OFF
  def is =
    "The Formatter should"                    ^
      "convert 'test'"                        ! to("test", "test")^
      "convert 1"                             ! to(1, 1)^
      "convert List(1, 2)"                    ! to(List(1, 2), Array(1, 2))^
      "convert List('t', 'v')"                ! to(List("t", "v"), Array("t", "v"))
      // "convert List(1, 2)"                    ! from(List(1, 2))^
    end



  /*def from[T](in: T)(implicit m: Manifest[T]) = {
    toAny(in) must beRight
    fromAny(toAny(in).right.get, m.erasure) must beRight
    fromAny(toAny(in).right.get, m.erasure).right.get.asInstanceOf[T] must_== in
  }*/

  /*def from[T](in: T)(implicit m: Manifest[T]) = {
    toAny(in) must beRight
    println(fromAny(toAny(in).right.get, m.erasure))
    fromAny(toAny(in).right.get, m.erasure).right.get.asInstanceOf[T] must_== in
  }*/
  
  def to[T, R](in: T, out: R)(implicit m: Manifest[T]) = {
    toUima(in) must beSome
    toUima(in).get must_== out
  }
}