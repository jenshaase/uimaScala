/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import weka.core.{ Instances, Attribute, SparseInstance, Instance }
import java.io.{ BufferedWriter, FileWriter, File }
import com.github.jenshaase.uimascala.toolkit.ml.{ Writer, Feature }
import com.github.jenshaase.uimascala.toolkit.ml.weka.WekaConverter._
import scala.collection.JavaConversions._

class ARFFWriter(file: File, meta: Meta) extends Writer {

  file.getParentFile().mkdirs()

  val instances = meta.createInstances

  val writer = new BufferedWriter(new FileWriter(file))
  writer.write(instances.toString)

  def write(features: Iterable[Feature[_]], outcome: Feature[_]) =
    writer.write(featuresToInstance(features ++ Seq(outcome), meta).toString + "\n")

  def finish() = writer.close()
}