package com.github.jenshaase.uimascala.examples.ex1

import com.github.jenshaase.uimascala.core.description._

@TypeSystemDescription
object TypeSystem {

  val RoomNumber = Annotation {
    val building = Feature[String]
  }

}
