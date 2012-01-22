/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala.core

import xml.Node

trait XmlDescriptor {
  def xmlType: String
  def toXml: Node
}