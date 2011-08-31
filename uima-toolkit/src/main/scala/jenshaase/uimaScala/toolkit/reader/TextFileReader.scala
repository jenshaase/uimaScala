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

package jenshaase.uimaScala.toolkit.reader

import jenshaase.uimaScala.core.SCasCollectionReader_ImplBase
import jenshaase.uimaScala.core.configuration.parameter._
import java.util.regex.Pattern
import org.apache.uima.UimaContext
import org.apache.uima.jcas.JCas
import jenshaase.uimaScala.toolkit.types.DocumentAnnotation
import org.apache.uima.util.ProgressImpl
import java.io.File
import scala.collection.mutable.Queue


/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
class TextFileReader extends SCasCollectionReader_ImplBase {

  object path extends FileParameter(this)
  
  object filenamePattern extends OptionalPatternParameter(this) {
    override def defaultValue = Some(Pattern.compile(".*\\.txt"))
  }
  
  object locale extends OptionalLocaleParameter(this)
  
  var files: Queue[File] = null
  
  var total: Int = 0

  override def initialize(context: UimaContext) = {
    files = collectFiles(path.is)
    total = files.size
  }

  def getNext(cas: JCas) = {
    val file = files.dequeue

    locale.is.map(l => cas.setDocumentLanguage(l.getLanguage))
    cas.setDocumentText(scala.io.Source.fromFile(file).mkString)
    val doc = new DocumentAnnotation(cas,0,0)
    doc.setName(file.getName)
    doc.setSource(file.getAbsolutePath)
    doc.addToIndexes
  }
  
  def hasNext =
    !files.isEmpty
  
  def getProgress =
    Array(new ProgressImpl(total - files.size, total, "file"))
  
  def collectFiles(path: File): Queue[File] = {
    val files = path.listFiles
    Queue() ++= files.
    	filter { f => 
    	  f.isFile &&
    	  	filenamePattern.is.map(_.matcher(f.getName).matches).getOrElse(true) 
    	} ++ files.filter(_.isDirectory).flatMap(collectFiles)
  }
}