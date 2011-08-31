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
import jenshaase.uimaScala.toolkit.types.{Stopword, Token}
import org.uimafit.descriptor.ConfigurationParameter
import java.io.File
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngine
import org.uimafit.factory.AnalysisEngineFactory
import jenshaase.uimaScala.core.configuration.parameter.FileParameter
import scala.io.Source


class StopwordTagger extends SCasAnnotator_ImplBase {

  object stopwordFile extends FileParameter(this) {
    def loadStopwords: Set[String] = 
      Source.fromFile(is).getLines.map(_.toLowerCase).toSet
  }
  
  protected var stopwords: Set[String] = _

  override def initialize(context: UimaContext) = {
    super.initialize(context)

    stopwords = stopwordFile.loadStopwords
  }

  def process(cas: JCas) = {
    cas.select(classOf[Token]).foreach(t => {
      if (stopwords.contains(t.getCoveredText.toLowerCase)) {
        new Stopword(cas, t.getBegin, t.getEnd).addToIndexes
      }
    })
  }
}

class StopwordRemover extends StopwordTagger {

  override def process(cas: JCas) = {
    cas.select(classOf[Token]).
      filter(t => stopwords.contains(t.getCoveredText.toLowerCase)).
      foreach(_.removeFromIndexes)
  }
}