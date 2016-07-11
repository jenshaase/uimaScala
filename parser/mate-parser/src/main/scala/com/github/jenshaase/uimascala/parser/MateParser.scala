package com.github.jenshaase.uimascala.parser

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import scala.collection.JavaConversions._
import org.apache.uima.resource.DataResource
import org.apache.uima.resource.SharedResourceObject
import is2.data.SentenceData09
import is2.io.CONLLReader09
import is2.io.IOGenerals
import is2.parser.Options
import is2.parser.Parser

class MateParserResource extends SharedResourceObject {
  private var parser: Parser = _

  def load(data: DataResource) {
    val uri = data.getUri.toString

    if (new java.io.File(uri).exists) {
      parser = new Parser(new Options(Array("-model", uri)))
    } else {
      val resourceUri = if (uri.startsWith("/")) uri else "/" + uri
      val resource = this.getClass.getResource(resourceUri)

      val file = java.io.File.createTempFile("mate-parser", ".temp")
      file.deleteOnExit();

      val source = resource.openStream();
      try {
        java.nio.file.Files.copy(source, file.toPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      } finally {
        source.close();
      }

      parser = new Parser(new Options(Array("-model", file.getAbsolutePath)))
    }
  }

  def getParser = parser
}

class MateParser extends SCasAnnotator_ImplBase {

  object model extends SharedResource[MateParserResource]("")

  def process(jcas: JCas) = {
    jcas.select[Sentence].foreach { sentence =>
      val tokens = jcas.selectCovered[Token](sentence).toVector

      val sentenceData = new SentenceData09()
      sentenceData.init(Array[String](IOGenerals.ROOT) ++ tokens.map(_.getCoveredText))
      sentenceData.setLemmas(Array[String](IOGenerals.ROOT_LEMMA) ++ tokens.map { t =>
        if (t.getLemma != null) {
          t.getLemma.getValue()
        } else {
          "_"
        }
      })
      sentenceData.setPPos(Array[String](IOGenerals.ROOT_POS) ++ tokens.map { t =>
        t.getPos.getName()
      })

      val parsed = model.resource.getParser.apply(sentenceData)

      parsed.labels.zipWithIndex.foreach { case (label, i) =>
        if (parsed.pheads(i) != 0) {
          val sourceToken = tokens(parsed.pheads(i) - 1)
          val targetToken = tokens(i)
          val depType = parsed.plabels(i)

          val dep = new Dependency(jcas)
          dep.setGovernor(sourceToken)
          dep.setDependent(targetToken)
          dep.setDependencyType(depType)
          dep.setBegin(dep.getDependent().getBegin())
          dep.setEnd(dep.getDependent().getEnd())
          dep.addToIndexes()
        } else {
          val rootToken = tokens(i)

          val dep = new DependencyRoot(jcas)
          dep.setGovernor(rootToken)
          dep.setDependent(rootToken)
          dep.setDependencyType(parsed.plabels(i))
          dep.setBegin(dep.getDependent().getBegin())
          dep.setEnd(dep.getDependent().getEnd())
          dep.addToIndexes()
        }
      }
    }
  }
}
