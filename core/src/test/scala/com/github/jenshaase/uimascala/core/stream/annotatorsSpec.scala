package com.github.jenshaase.uimascala.core.stream

import org.specs2.mutable._
import scalaz._, Scalaz._
import scalaz.stream._
import scalaz.concurrent.Task
import org.apache.uima.jcas.tcas.Annotation
import com.github.jenshaase.uimascala.core._

class annotateSpec extends Specification {

  import annotators._

  "Annotators" should {

    val tokenizeText =
      casFromText |>
      whitespaceTokenizer[Annotation](false)

    "tokenize a document" in {
      val p = Process("this is a text", " and another text ") |>
        tokenizeText |>
        extractCas[List[String]] { cas =>
          cas.select[Annotation].drop(1).map(_.getCoveredText).toList
        }
 
      p.toList must be equalTo (List(
        List("this", "is", "a", "text"),
        List("and", "another", "text")
      ))
    }

    "remove stopwords" in {
      val p = Process("this is a text", " and another text ") |>
        tokenizeText |>
        removeStopwords[Annotation](s => Set("is", "a").contains(s)) |>
        extractCas[List[String]] { cas =>
          cas.select[Annotation].drop(1).map(_.getCoveredText).toList
        }
 
      p.toList must be equalTo (List(
        List("this", "text"),
        List("and", "another", "text")
      ))
    }
  }
}
