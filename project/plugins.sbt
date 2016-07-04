addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.3")

lazy val plugins = project in file(".") dependsOn(file("../uima-sbt"))
