/**
 * Copyright (C) 2011 Jens Haase
 */
package uimascala

import sbt._
import Keys._

import uimascala.SbtUimaPlugin._
import uimascala.SbtUimaKeys._
import com.typesafe.sbtscalariform.ScalariformPlugin
import ScalariformPlugin.{ format, formatPreferences }

object UimaScalaBuild extends Build {

    lazy val buildSettings = Seq(
        organization := "com.github.jenshaase.uimascala",
        version      := "0.3-SNAPSHOT",
        scalaVersion := "2.9.1"
    )

    lazy val uimascala = Project(
        id = "uimascala",
        base = file("."),
        settings = parentSettings ++ Unidoc.settings ++ uimaSettings ++ Seq(
            parallelExecution in GlobalScope := false,
            Unidoc.unidocExclude := Seq(examples.id)
        ),
        aggregate = Seq(core, toolkit, examples, plugin)
    )
    
    lazy val core = Project(
        id = "uimascala-core",
        base = file("uima-core"),
        settings = defaultSettings ++ uimaSettings ++ Seq(
            libraryDependencies ++= Seq(Dependency.uimafit, Dependency.specs2)
        )
    )

    lazy val toolkit = Project(
        id = "uimascala-toolkit",
        base = file("uima-toolkit"),
        settings = defaultSettings ++ uimaSettings ++ Seq(
            typeSystem := Seq(toolkitTypSystem)
        ),
        dependencies = Seq(core, uri("uima-sbt-plugin"))
    )
    
    lazy val examples = Project(
        id = "uimascala-examples",
        base = file("uima-examples"),
        settings = parentSettings,
        aggregate = Seq(examples_ex1, examples_ex2)
    )

    lazy val examples_ex1 = Project(
        id = "uimascala-examples-ex1",
        base = file("uima-examples/ex1"),
        settings = defaultSettings ++ uimaSettings,
        dependencies = Seq(toolkit)
    )

    lazy val examples_ex2 = Project(
        id = "uimascala-examples-ex2",
        base = file("uima-examples/ex2"),
        settings = defaultSettings ++ uimaSettings,
        dependencies = Seq(toolkit)
    )

    lazy val plugin = Project(
        id = "uimascala-sbt-plugin",
        base = file("uima-sbt-plugin"),
        settings = defaultSettings ++ Seq(
            sbtPlugin := true,
            libraryDependencies ++= Seq(Dependency.uimatools)
        )
    )
    
    // Settings

    override lazy val settings = super.settings ++ buildSettings ++ Publish.versionSettings

    lazy val baseSettings = Defaults.defaultSettings ++ Publish.settings

    lazy val parentSettings = baseSettings ++ Seq(
        publishArtifact in Compile := false
    )

    lazy val defaultSettings = baseSettings ++ formatSettings ++ Seq(
        scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
        libraryDependencies += Dependency.specs2
    )

    lazy val formatSettings = ScalariformPlugin.settings ++ Seq(
        formatPreferences in Compile := formattingPreferences,
        formatPreferences in Test := formattingPreferences
    )

    def formattingPreferences = {
        import scalariform.formatter.preferences._
        FormattingPreferences()
            .setPreference(RewriteArrowSymbols, true)
            .setPreference(AlignParameters, true)
            .setPreference(AlignSingleLineCaseStatements, true)
            .setPreference(IndentSpaces, 2)
            .setPreference(DoubleIndentClassDeclaration, true)
    }


    // Type description

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


object Dependency {
    
    val uimafit = "org.uimafit" % "uimafit" % "1.2.0"

    val uimatools = "org.apache.uima" % "uimaj-tools" % "2.3.1"

    // Testing
    val specs2 = "org.specs2" %% "specs2" % "1.6.1" % "test"
}