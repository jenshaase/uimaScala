resolvers += "uimaScala plugin repo" at "http://jenshaase.github.com/maven"

addSbtPlugin("com.github.jenshaase.uimascala" % "uimascala-sbt-plugin" % "0.4.0-SNAPSHOT")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.0.1")
