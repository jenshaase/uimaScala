/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import org.specs2.mutable.Specification
import java.util.Locale

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
class TestConvertingSpec extends Specification {

  "Test Converting" should {
    import Converter._

    "convert String" in {
      toUima("test").getClass.getName must_== "java.lang.String"
    }

    "convert List" in {
      List("test", "test2").toArray.getClass.getName must_== "[Ljava.lang.String;"
      toUima(List("test", "test")).getClass.getName must_== "[Ljava.lang.String;"
    }

    "convert Locale" in {
      toUima(new Locale("de")) must_== "de"
      toUima(List(new Locale("de"), new Locale("en"))) must_== Array("de", "en")
    }

    "converts in a parameter" in {
      object s extends Parameter[String]("test", "default")
      object l extends Parameter[List[Locale]](List(new Locale("de"), new Locale("en")), List(new Locale("de")))

      s.toUimaType must_== "test"
      l.toUimaType must_== Array("de", "en")
    }

    "converts back" in {
      object s extends Parameter[String]("test", "default")
      object l extends Parameter[List[Locale]](List(new Locale("de"), new Locale("en")), List(new Locale("de")))

      s.fromUimaType("test") must_== Some("test")
      l.fromUimaType(Array("de", "en")) must_== Some(List(new Locale("de"), new Locale("en")))
    }
  }

  class Parameter[T](var value: T, val default: T)(implicit m: Manifest[T]) {
    type ValueType = T

    import Converter._

    def :=(v: T) =
      value = v

    def toUimaType = toUima(value)
    def fromUimaType(v: Any): Option[T] = fromUima[T](v)
  }

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

  object Converter {
    var convertSeq: Seq[Caster[_, _]] = Seq.empty
    register(new Caster[String, String] {
      def toUimaType(in: String) = in
      def fromUimaType(in: Any) = in match {
        case s: String ⇒ Some(s)
        case _         ⇒ None
      }
    })
    register(new Caster[Int, Int] {
      def toUimaType(in: Int): Int = in
      def fromUimaType(in: Any) = in match {
        case i: Int ⇒ Some(i)
        case _      ⇒ None
      }
    })
    register(new Caster[Locale, String] {
      def toUimaType(in: Locale): String = in.getLanguage
      def fromUimaType(in: Any) = in match {
        case l: Locale ⇒ Some(l)
        case s: String ⇒ Some(new Locale(s))
        case _         ⇒ None
      }
    })

    def toUima[A](in: A)(implicit m: Manifest[A]) =
      convertSeq.map(_.convertToUimaType(in)).find(_.isDefined).get.get

    def fromUima[A](in: Any)(implicit m: Manifest[A]): Option[A] = {
      convertSeq.map(c ⇒ c.convertFromUimaType[A](in)).find(_.isDefined).get.map(_.asInstanceOf[A])
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

  /*object Caster {
    val casterMap: Map[Class[_], UimaType[_, _]] = Map(
      classOf[String] -> StringUimaType)

    def toUima[T](in: T) = casterMap.get(in.getClass).get.toUimaType(in)
    def listToUima[T](in: List[T]) = in.map(toUima).toArray
  }

  trait UimaType[S, U] {
    def toUimaType(in: S): U
    def fromUimaType(in: U): S
  }

  object StringUimaType extends UimaType[String, String] {
    def toUimaType(in: String) = in
    def fromUimaType(in: String) = in
  }

  object IntUimaType extends UimaType[Int, Int] {
    def toUimaType(in: Int) = in
    def fromUimaType(in: Int) = in
  }

  trait ListUimaType[T] extends UimaType[List[T], Array[T]] {
    def toUimaType(in: List[T]) = in.toArray
    def fromUimaType(in: Array[T]) = in.toList
  }*/
}