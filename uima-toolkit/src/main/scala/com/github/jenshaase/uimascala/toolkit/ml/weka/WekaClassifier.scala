/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.toolkit.ml.weka

import com.github.jenshaase.uimascala.toolkit.ml.{ Classifier, Feature }
import com.github.jenshaase.uimascala.toolkit.ml.weka.WekaConverter._
import weka.core.Instances
import weka.classifiers.{ Classifier ⇒ WClassifier }
import java.io.{ BufferedReader, File, FileReader }

class WekaClassifier(c: WClassifier, arffFile: File, meta: Meta, th: Option[Double] = None) extends Classifier {

  val instances = new Instances(new BufferedReader(new FileReader(arffFile)))
  instances.setClass(instances.attribute(meta.classAttribute.name))
  c.buildClassifier(instances)

  def classify(features: Iterable[Feature[_]]): Option[String] = {
    val inst = featuresToInstance(features, meta, Some(instances))
    th.map { t ⇒
      c.distributionForInstance(inst).zipWithIndex.maxBy(_._1) match {
        case (dist, index) if dist >= t ⇒ Some(inst.classAttribute.value(index))
        case _                          ⇒ None
      }
    }.getOrElse {
      val res = c.classifyInstance(inst)
      Some(inst.classAttribute.value(res.toInt))
    }
  }
}