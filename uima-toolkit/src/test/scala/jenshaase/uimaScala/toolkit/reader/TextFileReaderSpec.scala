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

import org.specs2.mutable.Specification
import org.apache.uima.collection.CollectionReader
import org.apache.uima.util.CasCreationUtils
import org.apache.uima.jcas.JCas
import jenshaase.uimaScala.core.Implicits._
import jenshaase.uimaScala.toolkit.types.DocumentAnnotation

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */

class TextFileReaderSpec extends Specification {
  
  "Text file reader" should {
    "add document annotation" in {
      val reader = TextFileReader("uima-toolkit/src/test/resources/reader/textFileReader/test1")

      val it = new ReaderIterator(reader)
      it.hasNext must beTrue
      val doc = it.next.selectByIndex(classOf[DocumentAnnotation], 0)
      doc.getName must be equalTo("file2.txt")
      doc.getSource.endsWith("src/test/resources/reader/textFileReader/test1/file2.txt") must beTrue
    }

    "read text files from a directory" in {
      val reader = TextFileReader("uima-toolkit/src/test/resources/reader/textFileReader/test1")
      
      val it = new ReaderIterator(reader)
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo("file2.txt")
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo("file1.txt")
      it.hasNext must beFalse
    }
    
    "read directories recursivly" in {
      val reader = TextFileReader("uima-toolkit/src/test/resources/reader/textFileReader/test2")
      
      val it = new ReaderIterator(reader)
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo("file2.txt")
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo("file1.txt")
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo("subfile1.txt")
      it.hasNext must beFalse
    }
    
    "read only files that match the pattern" in {
      val reader = TextFileReader("uima-toolkit/src/test/resources/reader/textFileReader/test3")
      
      val it = new ReaderIterator(reader)
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo("file2.txt")
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo("file1.txt")
      it.hasNext must beFalse
    }
  }
}

class ReaderIterator(reader: CollectionReader) extends Iterator[JCas] {
  import scala.collection.JavaConversions._
  
  def next() = {
    val cas = CasCreationUtils.createCas(List(reader.getMetaData))
    reader.getNext(cas)
    cas.getJCas
  }

  def hasNext = reader.hasNext
}