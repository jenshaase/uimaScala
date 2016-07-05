package com.github.jenshaase.uimascala.segmenter

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import scala.util.matching.Regex

/**
  * @author Jens Haase <je.haase@googlemail.com>
  */
class RegexTokenizer extends SCasAnnotator_ImplBase {

  object regex extends Parameter[Regex]("""\s+""".r)
  object allowEmptyToken extends Parameter[Boolean](false)

  def process(jcas: JCas) = {
    val txt = jcas.getDocumentText

    val mostlyAll = getRegex.findAllMatchIn(txt).foldLeft(0) {
      case (last, m) if ((allowEmptyToken.is && m.start >= last) || (!allowEmptyToken.is && m.start > last)) =>
        add(createToken(jcas, last, m.start))
        m.end
      case (_, m) =>
        m.end
    }

    if (mostlyAll < txt.length)
      add(createToken(jcas, mostlyAll, txt.length))
  }

  protected def getRegex =
    regex.is

  protected def createToken(cas: JCas, begin: Int, end: Int) =
    new Token(cas, begin, end)
}
