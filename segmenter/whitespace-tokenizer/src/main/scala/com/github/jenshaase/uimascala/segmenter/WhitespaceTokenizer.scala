package com.github.jenshaase.uimascala.segmenter

import com.github.jenshaase.uimascala.core.configuration._
import scala.util.matching.Regex

class WhitespaceTokenizer extends RegexTokenizer {

  override def getRegex = """\s+""".r
}
