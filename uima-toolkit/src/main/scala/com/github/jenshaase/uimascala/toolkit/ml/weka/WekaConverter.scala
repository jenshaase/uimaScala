/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import weka.core.{ Instances, SparseInstance, Attribute, Instance }
import com.github.jenshaase.uimascala.toolkit.ml.Feature

object WekaConverter {

  import scala.collection.JavaConversions._

  def convertAttribute(attr: WekaAttribute): Attribute = attr match {
    case NorminalAttribute(name, norminals, fallback) ⇒ new Attribute(name, norminals.toList :+ fallback, 0)
    case ClassAttribute(name, norminals)              ⇒ new Attribute(name, norminals.toList, 0)
    case n: NumericAttribute                          ⇒ new Attribute(n.name)
    case DateAttribute(name, format)                  ⇒ new Attribute(name, format)
  }

  def featuresToInstance(features: Iterable[Feature[_]], meta: Meta, instances: Option[Instances] = None) = {
    val insts = instances.getOrElse(meta.createInstances)
    val inst = new SparseInstance(insts.numAttributes())
    inst.setDataset(insts)

    features filter { f ⇒ meta.attributeIndex.contains(f.name) } foreach { f ⇒
      applyValue(inst, f, meta)
    }

    inst
  }

  private def applyValue(inst: Instance, f: Feature[_], meta: Meta) = {
    val index = meta.createInstances.attribute(f.name).index

    f match {
      case n: NorminalFeature         ⇒ inst.setValue(index, norminalValue(n, meta))
      case n: ClassFeature            ⇒ inst.setValue(index, n.value)
      case DoubleFeature(name, value) ⇒ inst.setValue(index, value)
      case FloatFeature(name, value)  ⇒ inst.setValue(index, value.toDouble)
      case IntFeature(name, value)    ⇒ inst.setValue(index, value.toDouble)
      case LongFeature(name, value)   ⇒ inst.setValue(index, value.toDouble)
      case _                          ⇒ inst.setValue(index, f.value.toString)
    }
  }

  private def norminalValue(f: NorminalFeature, m: Meta) =
    m.norminalIndex.get(f.name).map { a ⇒
      if (a.norminals.contains(f.value)) f.value else a.fallback
    }.getOrElse("XXX")
}