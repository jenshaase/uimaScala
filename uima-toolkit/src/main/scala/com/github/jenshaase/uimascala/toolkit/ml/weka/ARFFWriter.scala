/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import weka.core.{ Instances, Attribute, SparseInstance, Instance }
import java.io.{ BufferedWriter, FileWriter, File }
import com.github.jenshaase.uimascala.toolkit.ml.weka.AttributeConverter._
import com.github.jenshaase.uimascala.toolkit.ml.{ Writer, Feature }
import scala.collection.JavaConversions._

class ARFFWriter(file: File, relation: String, attributes: List[WekaAttribute], classAttribute: WekaAttribute) extends Writer {

  val allAttributes = attributes :+ classAttribute

  val instances = new Instances(relation, new java.util.ArrayList[Attribute](allAttributes.map(convert _)), 0)

  file.getParentFile().mkdirs()

  val writer = new BufferedWriter(new FileWriter(file))
  writer.write(instances.toString)

  private val attrIndex = allAttributes.map { attr ⇒
    attr.name -> attr
  }.toMap

  private val norminalIndex = allAttributes.collect {
    case n @ NorminalAttribute(name, _, _) ⇒ name -> n
  }.toMap

  def write(features: Iterable[Feature[_]], outcome: Feature[_]) = {
    val inst = new SparseInstance(instances.numAttributes())
    inst.setDataset(instances)

    features filter { f ⇒ attrIndex.contains(f.name) } foreach { f ⇒
      setValue(inst, f)
    }

    setValue(inst, outcome)

    writer.write(inst.toString + "\n")
  }

  def finish() = writer.close()

  private def setValue(inst: Instance, f: Feature[_]) = {
    val index = instances.attribute(f.name).index

    f match {
      case n: NorminalFeature         ⇒ inst.setValue(index, norminalValue(n))
      case DoubleFeature(name, value) ⇒ inst.setValue(index, value)
      case FloatFeature(name, value)  ⇒ inst.setValue(index, value.toDouble)
      case IntFeature(name, value)    ⇒ inst.setValue(index, value.toDouble)
      case LongFeature(name, value)   ⇒ inst.setValue(index, value.toDouble)
      case _                          ⇒ inst.setValue(index, f.value.toString)
    }
  }

  private def norminalValue(f: NorminalFeature) =
    norminalIndex.get(f.name).map { a ⇒
      if (a.norminals.contains(f.value)) f.value else a.fallback
    }.getOrElse("XXX")
}