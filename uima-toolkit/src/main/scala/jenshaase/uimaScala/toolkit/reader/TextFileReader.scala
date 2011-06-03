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

import org.uimafit.component.JCasCollectionReader_ImplBase
import org.apache.uima.jcas.JCas
import org.apache.uima.UimaContext
import java.io.File
import org.uimafit.descriptor.ConfigurationParameter
import collection.mutable.Queue
import org.apache.uima.util.ProgressImpl
import java.util.regex.Pattern
import org.apache.uima.resource.metadata.TypeSystemDescription
import jenshaase.uimaScala.toolkit.configuration._
import org.uimafit.factory.{TypeSystemDescriptionFactory, CollectionReaderFactory}
import org.apache.uima.collection.CollectionReader
import jenshaase.uimaScala.toolkit.types.DocumentAnnotation

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
class TextFileReader extends JCasCollectionReader_ImplBase with LocaleConfig {

  @ConfigurationParameter(name=TextFileReader.PARAM_PATH, mandatory=true)
  protected var path: File = null
  
  @ConfigurationParameter(name=TextFileReader.PARAM_FILENAME_PATTERN, mandatory=true)
  protected var filenamePattern: Pattern = Pattern.compile(".*\\.txt")
  
  var files: Queue[File] = null
  
  var total: Int = 0

  override def initialize(context: UimaContext) = {
    files = collectFiles(path)
    total = files.size
  }

  def getNext(cas: JCas) = {
    val file = files.dequeue

    cas.setDocumentLanguage(getLocale.getLanguage)
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
    Queue() ++= files.filter(
      f => f.isFile && 
        filenamePattern.matcher(f.getName).matches) ++ 
      files.filter(_.isDirectory).flatMap(collectFiles)
  }
}

object TextFileReader {
  final val PARAM_PATH = "Path"
  final val PARAM_FILENAME_PATTERN = "FilenamePattern"
  
  def apply(path: String, pattern: String = ".*\\.txt", locale: String = "en"): CollectionReader =
    apply(TypeSystemDescriptionFactory.createTypeSystemDescription, path, pattern, locale)
  
  def apply(typeSystem: TypeSystemDescription, path: String, pattern: String, locale: String): CollectionReader = {
    CollectionReaderFactory.createCollectionReader(classOf[TextFileReader], typeSystem,
      PARAM_PATH, path,
      PARAM_FILENAME_PATTERN, pattern,
      Configuration.PARAM_LOCALE, locale
    )
  }
}