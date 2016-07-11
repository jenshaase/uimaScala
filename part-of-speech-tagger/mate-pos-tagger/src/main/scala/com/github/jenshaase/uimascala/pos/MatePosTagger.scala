package com.github.jenshaase.uimascala.pos

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource
import scala.collection.JavaConversions._
import is2.data.SentenceData09
import is2.io.CONLLReader09
import is2.io.IOGenerals
import is2.tag.Options
import is2.tag.Tagger

class MatePosTaggerResource extends SharedResourceObject {
  private var tagger: Tagger = _

  def load(data: DataResource) {
    val uri = data.getUri.toString

    if (new java.io.File(uri).exists) {
      tagger = new Tagger(new Options(Array("-model", uri)))
    } else {
      val resourceUri = if (uri.startsWith("/")) uri else "/" + uri
      val resource = this.getClass.getResource(resourceUri)

      val file = java.io.File.createTempFile("mate-pos-tagger", ".temp")
      file.deleteOnExit();

      val source = resource.openStream();
      try {
        java.nio.file.Files.copy(source, file.toPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      } finally {
        source.close();
      }

      tagger = new Tagger(new Options(Array("-model", file.getAbsolutePath)))
    }
  }

  def getTagger = tagger
}

class MatePosTagger extends SCasAnnotator_ImplBase {

  object model extends SharedResource[MatePosTaggerResource]("")
  object maxTokensPerSentence extends Parameter[Option[Int]](None) {
    override def mandatory_? = false
  }

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

      model.resource.getTagger.apply(sentenceData).ppos.drop(1).zipWithIndex.foreach { case (tag, idx) =>
        val token = tokens(idx)

        val pos = new POS(jcas, token.getBegin, token.getEnd)
        pos.setName(tag)
        add(pos)

        token.setPos(pos)
      }
    }
  }
}
