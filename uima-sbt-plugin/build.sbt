sbtPlugin := true

organization := "com.github.jenshaase.uimascala"

name := "uimascala-sbt-plugin"

version := "0.3-SNAPSHOT"

scalacOptions += "-deprecation"

libraryDependencies += "org.apache.uima" % "uimaj-tools" % "2.3.1"

publishMavenStyle := true

publishTo := Some(Resolver.file("Local", Path.userHome / "programming" / "jenshaase.github.com" / "maven" asFile)(Patterns(true, Resolver.mavenStyleBasePattern)))