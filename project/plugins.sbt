addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

lazy val plugins = project in file(".") dependsOn(file("../sbt-plugin"))
