package com.github.jenshaase.uimascala.core.stream

import org.specs2.mutable._
import fs2._
import org.apache.uima.jcas.tcas.Annotation
import com.github.jenshaase.uimascala.core._

class annotateSpec extends Specification {

  import annotators._

  "Annotators" should {

    def tokenizeText[F[_]] =
      casFromText[F] andThen whitespaceTokenizer[F, Annotation](false)

    "tokenize a document" in {
      val p = Stream.pure("this is a text", " and another text ").
        through(tokenizeText).
        through(extractCas { cas =>
          cas.select[Annotation].drop(1).map(_.getCoveredText).toList
        })
 
      p.toList must be equalTo (List(
        List("this", "is", "a", "text"),
        List("and", "another", "text")
      ))
    }

    "remove stopwords" in {
      val p = Stream.pure("this is a text", " and another text ").
        through(tokenizeText).
        through(removeStopwords[Pure, Annotation](s => Set("is", "a").contains(s))).
        through(extractCas { cas =>
          cas.select[Annotation].drop(1).map(_.getCoveredText).toList
        })
 
      p.toList must be equalTo (List(
        List("this", "text"),
        List("and", "another", "text")
      ))
    }
  }
}
