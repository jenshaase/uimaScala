package com.github.jenshaase.uimascala.lemmatizer

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
import is2.lemmatizer.Lemmatizer

class MateLemmatizerResource extends SharedResourceObject {
  private var lemmatizer: Lemmatizer = _

  def load(data: DataResource) {
    val uri = data.getUri.toString

    if (new java.io.File(uri).exists) {
      lemmatizer = new Lemmatizer(uri)
    } else {
      val resourceUri = if (uri.startsWith("/")) uri else "/" + uri
      val resource = this.getClass.getResource(resourceUri)

      val file = java.io.File.createTempFile("mate-lemmatizer", ".temp")
      file.deleteOnExit();

      val source = resource.openStream();
      try {
        java.nio.file.Files.copy(source, file.toPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      } finally {
        source.close();
      }

      lemmatizer = new Lemmatizer(file.getAbsolutePath)
    }
  }

  def getLemmatizer = lemmatizer
}

class MateLemmatizer extends SCasAnnotator_ImplBase {

  object model extends SharedResource[MateLemmatizerResource]("")

  def process(jcas: JCas) = {
    jcas.select[Sentence].foreach { sentence =>
      val tokens = jcas.selectCovered[Token](sentence).toVector

      val sentenceData = new SentenceData09()
      sentenceData.init(Array[String](IOGenerals.ROOT) ++ tokens.map(_.getCoveredText))

      model.resource.getLemmatizer.apply(sentenceData).plemmas.zipWithIndex.foreach { case (tag, idx) =>
        val token = tokens(idx)

        val lemma = new Lemma(jcas, token.getBegin, token.getEnd)
        lemma.setValue(tag)
        add(lemma)

        token.setLemma(lemma)
      }
    }
  }
}

