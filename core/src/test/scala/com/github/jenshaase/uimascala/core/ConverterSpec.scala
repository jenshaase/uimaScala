/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core

import org.specs2._
import matcher._
import java.util.regex.Pattern
import java.util.Locale
import java.io.File
import scala.util.matching.Regex

class ConverterSpec extends Specification {
  import CastFactory._

  // format: OFF
  def is = s2"""
    The Formatter should
      convert string            ${convert("test", "test")}
      convert int               ${convert(1, 1)}
      convert float             ${convert(1.2f, 1.2f)}
      convert double            ${convert(1.2d, 1.2d)}
      convert boolean           ${convert(true, true)}
      convert locale            ${convert(new Locale("en"), "en")}
      convert pattern           ${convert(Pattern.compile("[A-Z]*"), "[A-Z]*", Some(patToString _))}
      convert regex             ${convert("[A-Z]*".r, "[A-Z]*", Some(regToString _))}
      convert file              ${convert(new File("/test/abc"), "/test/abc")}
      
      convert string list       ${convert(List("t", "v"), Array("t", "v"))}
      convert int list          ${convert(List(1, 2), Array(1, 2))}
      convert float list        ${convert(List(1.2f, 2.3f), Array(1.2f, 2.3f))}
      convert double list       ${convert(List(1.2d, 2.3d), Array(1.2d, 2.3d))}
      convert boolean list      ${convert(List(true, false, true), Array(true, false, true))}
      convert pattern list      ${convert(List(Pattern.compile("[A-Z]"), Pattern.compile("[1-4]")), Array("[A-Z]", "[1-4]"), Some{in: List[Pattern] => in.map(patToString)})}
      convert regex list        ${convert(List("[A-Z]".r, "[1-4]".r), Array("[A-Z]", "[1-4]"), Some{in: List[Regex] => in.map(regToString)})}
      convert file list         ${convert(List(new File("/test/a"), new File("/test/b")), Array("/test/a", "/test/b"))}
      convert locale list       ${convert(List(new Locale("de"), new Locale("en")), Array("de", "en"))}
      convert file list         ${convert(List(new File("/test/a"), new File("/test/b")), Array("/test/a", "/test/b"))}
      
      convert string Seq        ${convert(Seq("t", "v"), Array("t", "v"))}
      convert int Seq           ${convert(Seq(1, 2), Array(1, 2))}
      convert float Seq         ${convert(Seq(1.2f, 2.3f), Array(1.2f, 2.3f))}
      convert double Seq        ${convert(Seq(1.2d, 2.3d), Array(1.2d, 2.3d))}
      convert boolean Seq       ${convert(Seq(true, false, true), Array(true, false, true))}
      convert pattern Seq       ${convert(Seq(Pattern.compile("[A-Z]"), Pattern.compile("[1-4]")), Array("[A-Z]", "[1-4]"), Some{in: Seq[Pattern] => in.map(patToString)})}
      convert regex Seq         ${convert(Seq("[A-Z]".r, "[1-4]".r), Array("[A-Z]", "[1-4]"), Some{in: Seq[Regex] => in.map(regToString)})}
      convert file Seq"         ${convert(Seq(new File("/test/a"), new File("/test/b")), Array("/test/a", "/test/b"))}
      convert locale Seq"       ${convert(Seq(new Locale("de"), new Locale("en")), Array("de", "en"))}
      convert file Seq"         ${convert(Seq(new File("/test/a"), new File("/test/b")), Array("/test/a", "/test/b"))}
      
      convert string option     ${convert(Some("test"), "test")}
      convert int option        ${convert(Some(1), 1)}
      convert float option      ${convert(Some(1.2f), 1.2f)}
      convert double option     ${convert(Some(1.2d), 1.2d)}
      convert boolean option    ${convert(Some(true), true)}
      convert locale option     ${convert(Some(new Locale("en")), "en")}
      convert pattern option    ${convert(Some(Pattern.compile("[A-Z]*")), "[A-Z]*", Some(optPatToString _))}
      convert regex option      ${convert(Some("[A-Z]*".r), "[A-Z]*", Some(optRegToString _))}
      convert file option       ${convert(Some(new File("/test/abc")), "/test/abc")}
      convert none option       ${convert(None, null)}
  """

  
  def convert[T, R](in: T, out: R, func: Option[T => _] = None)(implicit m: Manifest[T], r: Manifest[R]) = {
    val to = toUima(in)
    to must beRight
    to.right.get must beSome
    to.right.get.get must_== out

    val from = fromUima[T](out)
    from must beRight
    from.right.get must beSome
    func match {
      case Some(f) => f(from.right.get.get) must_== f(in)
      case None => from.right.get.get must_== in
    }
  }

  def patToString(in: Pattern) = in.pattern
  def regToString(in: Regex) = in.pattern.pattern
  def optPatToString(in: Option[Pattern]) = in.map(_.pattern)
  def optRegToString(in: Option[Regex]) = in.map(_.pattern.pattern)
}
