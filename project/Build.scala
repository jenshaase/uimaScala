import sbt._
import Keys._

import jenshaase.uimaScala.sbt._
import SbtUimaPlugin._

object UimaScalaBuild extends Build {

    lazy val root = Project(
        "root",
        file("."),
        settings = Defaults.defaultSettings ++ sharedSettings ++ Release.releaseSettings
    ) aggregate(uimaCore, uimaToolkit, uimaExamples)
    
    lazy val uimaCore = Project(
        "uima-scala-core",
        file("uima-core"),
        settings = Defaults.defaultSettings ++ sharedSettings ++ uimaSettings ++ coreSettings
    )

    lazy val uimaToolkit = Project(
        "uima-scala-toolkit",
        file("uima-toolkit"),
        settings = Defaults.defaultSettings ++ sharedSettings ++ uimaSettings ++ toolkitSettings
    ) dependsOn(uimaCore)
    
    lazy val uimaExamples = Project(
        "uima-scala-examples",
        file("uima-examples"),
        settings = Defaults.defaultSettings ++ sharedSettings ++ uimaSettings ++ exampleSettings
    ) dependsOn(uimaToolkit)
    
    lazy val sharedSettings = Seq(
        organization := "jenshaase",
        version := "0.3-SNAPSHOT",
        scalaVersion := "2.9.0-1",
        crossScalaVersions := Seq("2.9.0-1"),
        scalacOptions += "-deprecation",
        libraryDependencies ++= Seq(
            "org.uimafit" % "uimafit" % "1.2.0",
            "org.specs2" %% "specs2" % "1.3" % "test"
            ),
        shellPrompt := { "sbt (%s)> " format projectId(_) },
        publishMavenStyle := true,
        publishTo := Some(Resolver.file("Local", Path.userHome / "programming" / "jenshaase.github.com" / "maven" asFile)(Patterns(true, Resolver.mavenStyleBasePattern)))
    )
    
    lazy val coreSettings = Seq(
    )
    
    lazy val toolkitSettings = Seq(
        typeSystem := Seq(toolkitTypSystem)
    )
    
    lazy val exampleSettings = Seq(
    )

    lazy val toolkitTypSystem = UimaTypeSystem("uimaScalaToolkit")(
      _.description("Contains all type system descriptor for this toolkit"),

      _.withType("jenshaase.uimaScala.toolkit.types.DocumentAnnotation", UimaTyp.Annotation)(
        _.description("A annotation for the complete document"),
        _.withFeature("name", UimaTyp.String)(),
        _.withFeature("source", UimaTyp.String)()
      ),

      _.withType("jenshaase.uimaScala.toolkit.types.Token", UimaTyp.Annotation)(
        _.description("A simple token annotation")
      ),

      _.withType("jenshaase.uimaScala.toolkit.types.Sentence", UimaTyp.Annotation)(
        _.description("A simple sentence annotation")
      ),

      _.withType("jenshaase.uimaScala.toolkit.types.Stopword", UimaTyp.Annotation)(
        _.description("A stopword annotation")
      )
    )
    
    // Helpers
    def projectId(state: State) = extracted(state).currentProject.id
    def extracted(state: State) = Project extract state
}
