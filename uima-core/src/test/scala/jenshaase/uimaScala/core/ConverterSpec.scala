/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import org.specs2._
import matcher._
import java.util.regex.Pattern
import java.util.Locale
import java.io.File
import scala.util.matching.Regex

class ConverterSpec extends Specification with DataTables {
  import CastFactory._

  // format: OFF
  def is =
    "The Formatter should"                    ^
      "convert string"                        ! convert("test", "test")^
      "convert int"                           ! convert(1, 1)^
      "convert float"                         ! convert(1.2f, 1.2f)^
      "convert boolean"                       ! convert(true, true)^
      "convert locale"                        ! convert(new Locale("en"), "en")^
      "convert pattern"                       ! convertPattern(Pattern.compile("[A-Z]*"), "[A-Z]*")^
      "convert regex"                         ! convertRegex("[A-Z]*".r, "[A-Z]*")^
      "convert file"                          ! convert(new File("/test/abc"), "/test/abc")^
      "convert string list"                   ! convert(List("t", "v"), Array("t", "v"))^
      "convert int list"                      ! convert(List(1, 2), Array(1, 2))^
      "convert float list"                    ! convert(List(1.2f, 2.3f), Array(1.2f, 2.3f))^
      "convert boolean list"                  ! convert(List(true, false, true), Array(true, false, true))^
      "convert locale list"                   ! convert(List(new Locale("de"), new Locale("en")), Array("de", "en"))^
      "convert file list"                     ! convert(List(new File("/test/a"), new File("/test/b")), Array("/test/a", "/test/b"))
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
  
  def convert[T, R](in: T, out: R)(implicit m: Manifest[T], r: Manifest[R]) = {
    val to = toUima(in)
    to must beRight
    to.right.get must beSome
    to.right.get.get must_== out

    val from = fromUima[T](out)
    from must beRight
    from.right.get must beSome
    from.right.get.get must_== in
  }

  def convertPattern(in: Pattern, out: String) = {
    val to = toUima(in)
    to must beRight
    to.right.get must beSome
    to.right.get.get must_== out

    val from = fromUima[Pattern](out)
    from must beRight
    from.right.get must beSome
    from.right.get.get.pattern must_== in.pattern
  }

  def convertRegex(in: Regex, out: String) = {
    val to = toUima(in)
    to must beRight
    to.right.get must beSome
    to.right.get.get must_== out

    val from = fromUima[Regex](out)
    from must beRight
    from.right.get must beSome
    from.right.get.get.pattern.pattern must_== in.pattern.pattern
  }
}