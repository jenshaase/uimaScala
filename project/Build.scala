/**
 * Copyright (C) 2011 Jens Haase
 */
package com.github.jenshaase.uimascala

import sbt._
import Keys._

import SbtUimaPlugin._
import SbtUimaKeys._
import sbtrelease.Release._
import scalariform.formatter.preferences._
import com.typesafe.sbtscalariform.ScalariformPlugin

object UimaScalaBuild extends Build {

    lazy val buildSettings = Seq(
        organization := "com.github.jenshaase.uimascala",
        scalaVersion := "2.9.1"
    )

    lazy val uimascala = Project(
        id = "uimascala",
        base = file("."),
        settings = parentSettings ++ uimaSettings ++ Seq(
            parallelExecution in GlobalScope := false
        ),
        aggregate = Seq(core, toolkit, examples)
    )
    
    lazy val core = Project(
        id = "uimascala-core",
        base = file("uima-core"),
        settings = defaultSettings ++ uimaSettings ++ projectReleaseSettings ++ Seq(
            libraryDependencies ++= Seq(Dependency.uimafit, Dependency.specs2)
        )
    )

    lazy val toolkit = Project(
        id = "uimascala-toolkit",
        base = file("uima-toolkit"),
        settings = defaultSettings ++ uimaSettings ++ projectReleaseSettings ++ Seq(
            typeSystem := Seq(toolkitTypSystem)
        ),
        dependencies = Seq(core)
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
    
    // Settings

    override lazy val settings = super.settings ++ buildSettings

    lazy val baseSettings = Defaults.defaultSettings

    lazy val parentSettings = baseSettings ++ Seq(
        publishArtifact in Compile := false
    )

    lazy val defaultSettings = baseSettings ++ formatSettings ++ Seq(
        scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
        libraryDependencies += Dependency.specs2
    )

    lazy val formatSettings = ScalariformPlugin.scalariformSettings ++ Seq(
        ScalariformPlugin.ScalariformKeys.preferences in Compile := formattingPreferences,
        ScalariformPlugin.ScalariformKeys.preferences in Test := formattingPreferences
    )

    lazy val projectReleaseSettings = releaseSettings ++ Seq(
        publishTo := Some(Resolver.file("Local", Path.userHome / "programming" / "jenshaase.github.com" / "maven" asFile))
    )

    def formattingPreferences = {
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
    
    val uimafit = "org.uimafit" % "uimafit" % "1.3.0"

    // Testing
    val specs2 = "org.specs2" %% "specs2" % "1.9" % "test"
}