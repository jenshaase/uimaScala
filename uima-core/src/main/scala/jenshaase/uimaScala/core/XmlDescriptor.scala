/**
 * Copyright (C) 2011 Jens Haase
 */
package jenshaase.uimaScala.core

import xml.Node

trait XmlDescriptor {
  def xmlType: String
  def toXml: Node
}