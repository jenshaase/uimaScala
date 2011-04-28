/*
 * Copyright (C) 2011 by Jens Haase <je.haase@googlemail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenshaase.uimaScala.toolkit.annotator

import jenshaase.uimaScala.core.SCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas
import jenshaase.uimaScala.core.Implicits._
import jenshaase.uimaScala.toolkit.types.{Stopword, Token}
import org.uimafit.descriptor.ConfigurationParameter
import java.io.File
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngine
import org.uimafit.factory.AnalysisEngineFactory


class StopwordTagger extends SCasAnnotator_ImplBase {

  @ConfigurationParameter(name=StopwordTagger.PARAM_STOPWORD_FILE, mandatory=true)
  protected var stopwordFile: String = null

  protected var stopwords: Set[String] = null

  override def initialize(context: UimaContext) = {
    super.initialize(context)
    if (stopwordFile == null) {
      throw new Exception("Filename is null")
    }
    stopwords = scala.io.Source.fromFile(stopwordFile).getLines.map(_.toLowerCase).toSet
  }

  def process(cas: JCas) = {
    cas.select(classOf[Token]).foreach(t => {
      if (stopwords.contains(t.getCoveredText.toLowerCase)) {
        new Stopword(cas, t.getBegin, t.getEnd).addToIndexes
      }
    })
  }
}

object StopwordTagger {
  final val PARAM_STOPWORD_FILE = "StopwordFile"

  def apply(file: String): AnalysisEngine = AnalysisEngineFactory.
    createPrimitive(classOf[StopwordTagger],
    PARAM_STOPWORD_FILE, file)

  def apply(stopwordFile: File): AnalysisEngine = apply(stopwordFile.getAbsolutePath)
}



class StopwordRemover extends StopwordTagger {

  override def process(cas: JCas) = {
    cas.select(classOf[Token]).
      filter(t => stopwords.contains(t.getCoveredText.toLowerCase)).
      foreach(_.removeFromIndexes)
  }
}

object StopwordRemover {
  final val PARAM_STOPWORD_FILE = "StopwordFile"

  def apply(file: String): AnalysisEngine = AnalysisEngineFactory.
    createPrimitive(classOf[StopwordRemover],
    PARAM_STOPWORD_FILE, file)

  def apply(stopwordFile: File): AnalysisEngine = apply(stopwordFile.getAbsolutePath)
}