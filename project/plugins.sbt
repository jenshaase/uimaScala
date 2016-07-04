addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")

lazy val plugins = project in file(".") dependsOn(file("../uima-sbt"))
