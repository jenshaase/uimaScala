package com.github.jenshaase.uimascala.core.stream

import scala.util.matching.Regex
import org.apache.uima.jcas.tcas.Annotation
import scala.reflect.ClassTag
import com.github.jenshaase.uimascala.core._
import org.apache.uima.jcas.JCas

trait annotators {

  @deprecated("Use com.github.jenshaase.uimascala.segmenter.RegexTokenizer")
  def regexTokenizer[F[_], T <: Annotation](pattern: Regex, allowEmptyToken: Boolean = true)(implicit cf: ClassTag[T]) =
    annotate[F] { cas: JCas =>
      val txt = cas.getDocumentText

      val mostlyAll = pattern.findAllMatchIn(txt).foldLeft(0) {
        case (last, m) if ((allowEmptyToken && m.start >= last) || (!allowEmptyToken && m.start > last)) ⇒
          cas.annotate[T](last, m.start)
          m.end
        case (_, m) =>
          m.end
      }

      if (mostlyAll < txt.length)
        cas.annotate[T](mostlyAll, txt.length)
    }

  @deprecated("Use com.github.jenshaase.uimascala.segmenter.WhitespaceTokenizer")
  def whitespaceTokenizer[F[_], T <: Annotation](allowEmptyToken: Boolean = true)(implicit cf: ClassTag[T]) =
    regexTokenizer[F, Annotation]("\\s+".r, allowEmptyToken)

  def removeStopwords[F[_], T <: Annotation](isStopword: String => Boolean)(implicit cf: ClassTag[T]) =
    annotate[F] { cas: JCas =>
      cas.select[T].
        filter { token => isStopword(token.getCoveredText) }.
        foreach { token => token.removeFromIndexes() }
    }

  def annotateStopwords[F[_], Token <: Annotation, Stopword <: Annotation](isStopword: String => Boolean)
  (implicit ct: ClassTag[Token], cs: ClassTag[Stopword]) =
    annotate[F] { cas: JCas =>
      cas.select[Token].foreach { token =>
        if (isStopword(token.getCoveredText)) {
          cas.annotate[Stopword](token.getBegin, token.getEnd)
        }
      }
    }
}

object annotators extends annotators
