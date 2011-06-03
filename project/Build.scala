import sbt._
import Keys._

import jenshaase.uimaScala.sbt._
import SbtUimaPlugin._

object UimaScalaBuild extends Build {

    override def projects = Seq(uimaCore, uimaToolkit, uimaExamples)
    
    lazy val uimaCore = Project("uima-core", file("uima-core"),
        settings = Defaults.defaultSettings ++ sharedSettings ++ uimaSettings ++ coreSettings)

    lazy val uimaToolkit = Project("uima-toolkit", file("uima-toolkit"),
        settings = Defaults.defaultSettings ++ sharedSettings ++ uimaSettings ++ toolkitSettings) dependsOn(uimaCore)
    
    lazy val uimaExamples = Project("uima-examples", file("uima-examples"),
        settings = Defaults.defaultSettings ++ sharedSettings ++ uimaSettings ++ exampleSettings) dependsOn(uimaToolkit)
    
    lazy val sharedSettings = Seq(
        organization := "jenshaase",
        name := "uimaScala",
        version := "0.2-SNAPSHOT",
        scalaVersion := "2.9.0-1",
        libraryDependencies ++= Seq(
            "org.uimafit" % "uimafit" % "1.2.0",
            "org.specs2" %% "specs2" % "1.3" % "test"
            )
    )
    
    lazy val coreSettings = Seq(
        name := "uimaScala-core"
    )
    
    lazy val toolkitSettings = Seq(
        name := "uimaScala-toolkit",
        typeSystem := Seq(toolkitTypSystem)
    )
    
    lazy val exampleSettings = Seq(
        name := "uimaScala-example"
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
}
