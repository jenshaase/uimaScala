/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import PartialFunction._
import java.util.regex.Pattern
import java.util.Locale
import java.io.File
import util.matching.Regex
import jenshaase.uimaScala.core.configuration._

abstract class Caster[In, Out](implicit in: Manifest[In], out: Manifest[Out]) {
  def convertToUimaType[X](c: X)(implicit m: Manifest[X]): Option[Any] = {
    def sameArgs = in.typeArguments.zip(m.typeArguments).forall {
      case (in, actual) ⇒ in >:> actual
    }

    if (in >:> m && sameArgs) Some(toUimaType(c.asInstanceOf[In]))
    else None
  }

  def convertFromUimaType[X](c: Any)(implicit m: Manifest[X]): Option[In] = {
    def sameArgs = in.typeArguments.zip(m.typeArguments).forall {
      case (in, actual) ⇒ in >:> actual
    }

    if (in >:> m && sameArgs) fromUimaType(c)
    else None
  }

  def toUimaType(in: In): Out
  def fromUimaType(in: Any): Option[In]
}

object CastFactory {

  import BasicCaster._

  var convertSeq: Seq[Caster[_, _]] = Seq.empty

  register(stringCaster)
  register(intCaster)
  register(floatCaster)
  register(booleanCaster)
  register(localeCaster)
  register(regexCaster)
  register(patternCaster)
  register(fileCaster)

  // TODO: Output error if not caster is found
  def toUima[A](in: A)(implicit m: Manifest[A]): Either[Failure, Option[Any]] =
    convertSeq.map(_.convertToUimaType(in)).find(_.isDefined) match {
      case Some(v) ⇒ Right(v)
      case None    ⇒ Left(Failure("Can not find a converter for: " + m.erasure.toString))
    }

  // TODO: Output error if not caster is found
  def fromUima[A](in: Any)(implicit m: Manifest[A]): Either[Failure, Option[A]] = {
    convertSeq.map(c ⇒ c.convertFromUimaType[A](in)).find(_.isDefined) match {
      case Some(v) ⇒ Right(v.map(_.asInstanceOf[A]))
      case None    ⇒ Left(Failure("Can not find a converter for: " + m.erasure.toString))
    }
  }

  def register[In, Out](c: Caster[In, Out])(implicit ml: Manifest[List[In]], m: Manifest[In], mo: Manifest[Out]) =
    convertSeq ++= Seq(c, buildListCaster(c))

  protected def buildListCaster[In, Out](c: Caster[In, Out])(implicit ml: Manifest[List[In]], m: Manifest[In], mo: Manifest[Out]) =
    new Caster[List[In], Array[Out]] {
      def toUimaType(in: List[In]) = in.map(c.toUimaType).toArray
      def fromUimaType(in: Any) = in match {
        case arr: Array[_] ⇒ sequence(arr.toList.map(c.fromUimaType))
        case _             ⇒ None
      }
    }

  def sequence[A](l: List[Option[A]]) =
    if (l.contains(None)) None else Some(l.flatten)

}

object BasicCaster {

  import java.util.Locale
  import java.util.regex.Pattern
  import scala.util.matching.Regex

  val stringCaster = new Caster[String, String] {
    def toUimaType(in: String) = in
    def fromUimaType(in: Any) = in match {
      case s: String ⇒ Some(s)
      case _         ⇒ None
    }
  }

  val intCaster = new Caster[Int, Int] {
    def toUimaType(in: Int): Int = in
    def fromUimaType(in: Any) = in match {
      case i: Int ⇒ Some(i)
      case _      ⇒ None
    }
  }

  val floatCaster = new Caster[Float, Float] {
    def toUimaType(in: Float): Float = in
    def fromUimaType(in: Any) = in match {
      case f: Float ⇒ Some(f)
      case _        ⇒ None
    }
  }

  val booleanCaster = new Caster[Boolean, Boolean] {
    def toUimaType(in: Boolean): Boolean = in
    def fromUimaType(in: Any) = in match {
      case b: Boolean ⇒ Some(b)
      case _          ⇒ None
    }
  }

  val localeCaster = new Caster[Locale, String] {
    def toUimaType(in: Locale): String = in.getLanguage
    def fromUimaType(in: Any) = in match {
      case l: Locale ⇒ Some(l)
      case s: String ⇒ Some(new Locale(s))
      case _         ⇒ None
    }
  }

  val regexCaster = new Caster[Regex, String] {
    def toUimaType(in: Regex): String = in.pattern.pattern
    def fromUimaType(in: Any) = in match {
      case l: Regex  ⇒ Some(l)
      case s: String ⇒ Some(s.r)
      case _         ⇒ None
    }
  }

  val patternCaster = new Caster[Pattern, String] {
    def toUimaType(in: Pattern): String = in.pattern
    def fromUimaType(in: Any) = in match {
      case l: Pattern ⇒ Some(l)
      case s: String  ⇒ Some(Pattern.compile(s))
      case _          ⇒ None
    }
  }

  val fileCaster = new Caster[File, String] {
    def toUimaType(in: File): String = in.getAbsolutePath
    def fromUimaType(in: Any) = in match {
      case f: File   ⇒ Some(f)
      case s: String ⇒ Some(new File(s))
      case _         ⇒ None
    }
  }
}