/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.toolkit.reader

import org.specs2.mutable.Specification
import org.apache.uima.collection.CollectionReader
import org.apache.uima.util.CasCreationUtils
import org.apache.uima.jcas.JCas
import jenshaase.uimaScala.core.Implicits._
import jenshaase.uimaScala.toolkit.types.DocumentAnnotation
import java.io.File

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */

class TextFileReaderSpec extends Specification {

  "Text file reader" should {
    "add document annotation" in {
      var reader = new TextFileReader().
        path(new File("uima-toolkit/src/test/resources/reader/textFileReader/test1")).
        asCollectionReader

      val it = new ReaderIterator(reader)
      it.hasNext must beTrue
      val doc: DocumentAnnotation = it.next.selectByIndex(classOf[DocumentAnnotation], 0)
      doc.getName must be equalTo ("file1.txt")
      doc.getSource.endsWith("src/test/resources/reader/textFileReader/test1/file1.txt") must beTrue
    }

    "read text files from a directory" in {
      val reader = new TextFileReader().
        path(new File("uima-toolkit/src/test/resources/reader/textFileReader/test1")).
        asCollectionReader

      val it = new ReaderIterator(reader)
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo ("file1.txt")
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo ("file2.txt")
      it.hasNext must beFalse
    }

    "read directories recursivly" in {
      val reader = new TextFileReader().
        path(new File("uima-toolkit/src/test/resources/reader/textFileReader/test2")).
        asCollectionReader

      val it = new ReaderIterator(reader)
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo ("file1.txt")
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo ("file2.txt")
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo ("subfile1.txt")
      it.hasNext must beFalse
    }

    "read only files that match the pattern" in {
      val reader = new TextFileReader().
        path(new File("uima-toolkit/src/test/resources/reader/textFileReader/test3")).
        asCollectionReader

      val it = new ReaderIterator(reader)
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo ("file1.txt")
      it.hasNext must beTrue
      it.next.getDocumentText must be equalTo ("file2.txt")
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