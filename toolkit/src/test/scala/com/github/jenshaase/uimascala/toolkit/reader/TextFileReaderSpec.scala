/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.reader

import java.io.File
import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.toolkit.description.DocumentAnnotation
import org.apache.uima.collection.CollectionReader
import org.apache.uima.jcas.JCas
import org.apache.uima.util.CasCreationUtils
import org.specs2.mutable.Specification

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */

class TextFileReaderSpec extends Specification {

  "Text file reader" should {
    "add document annotation" in {
      var reader = new TextFileReader().config(
        _.path := new File("toolkit/src/test/resources/reader/textFileReader/test1")).
        asCollectionReader

      val data = new ReaderIterator(reader).toList

      data.map(_.selectByIndex[DocumentAnnotation](0).getName).
        toList.sorted must be equalTo List("file1.txt", "file2.txt")

      data.map(_.selectByIndex[DocumentAnnotation](0).getSource.endsWith(".txt")).
        toList must be equalTo List(true, true)
    }

    "read directories recursivly" in {
      val reader = new TextFileReader().config(
        _.path := new File("toolkit/src/test/resources/reader/textFileReader/test2")).
        asCollectionReader

      new ReaderIterator(reader).
        map(_.selectByIndex[DocumentAnnotation](0).getName).
        toList.sorted must be equalTo List("file1.txt", "file2.txt", "subfile1.txt")
    }

    "read only files that match the pattern" in {
      val reader = new TextFileReader().config(
        _.path := new File("toolkit/src/test/resources/reader/textFileReader/test3")).
        asCollectionReader

      new ReaderIterator(reader).
        map(_.selectByIndex[DocumentAnnotation](0).getName).
        toList.sorted must be equalTo List("file1.txt", "file2.txt")
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
