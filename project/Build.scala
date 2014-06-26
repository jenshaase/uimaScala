/**
  * Copyright (C) 2011 Jens Haase
  */
package com.github.jenshaase.uimascala

import sbt._
import Keys._

import sbtrelease.ReleasePlugin._

object UimaScalaBuild extends Build {

  lazy val buildSettings = Seq(
    organization := "com.github.jenshaase.uimascala",
    scalaVersion := "2.11.1"
  )

  lazy val uimascala = Project(
    id = "uimascala",
    base = file("."),
    settings = parentSettings ++ Seq(
      parallelExecution in GlobalScope := false
    ),
    aggregate = Seq(core, toolkit, examples)
  )
  
  lazy val core = Project(
    id = "uimascala-core",
    base = file("uima-core"),
    settings = defaultSettings ++ projectReleaseSettings ++ Seq(
      libraryDependencies ++= Seq(
        Dependency.uimafit,
        Dependency.uimaTools,
        Dependency.specs2
      ),
      libraryDependencies <+= (scalaVersion)(
        "org.scala-lang" % "scala-reflect" % _
      ),
      libraryDependencies ++= (
        if (scalaVersion.value.startsWith("2.10")) List(Dependency.paradise) else Nil
      ),
      libraryDependencies ++= (
        if (scalaVersion.value.startsWith("2.11")) List(Dependency.scalaXml) else Nil
      ),
      addCompilerPlugin(Dependency.paradise)
    )
  )

  lazy val toolkit = Project(
    id = "uimascala-toolkit",
    base = file("uima-toolkit"),
    settings = defaultSettings ++ projectReleaseSettings ++ Seq(
      addCompilerPlugin(Dependency.paradise)
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
    settings = defaultSettings ++ Seq(
      addCompilerPlugin(Dependency.paradise)
    ),
    dependencies = Seq(toolkit)
  )

  lazy val examples_ex2 = Project(
    id = "uimascala-examples-ex2",
    base = file("uima-examples/ex2"),
    settings = defaultSettings ++ Seq(
      addCompilerPlugin(Dependency.paradise)
    ),
    dependencies = Seq(toolkit)
  )
  
  // Settings

  override lazy val settings = super.settings ++ buildSettings

  lazy val baseSettings = Defaults.defaultSettings

  lazy val parentSettings = baseSettings ++ Seq(
    publishArtifact in Compile := false
  )

  lazy val defaultSettings = baseSettings ++ Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8",
      "-deprecation", "-unchecked", "-feature"),
    libraryDependencies += Dependency.specs2
  )

  lazy val projectReleaseSettings = releaseSettings ++ Seq(
    //publishTo := Some(Resolver.file("Local", Path.userHome / "dev" / "jenshaase.github.com" / "maven" asFile))
  )
}


object Dependency {

  val paradiseVersion = "2.0.0"
  val paradise = "org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full

  val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.2"
  
  val uimafit = "org.apache.uima" % "uimafit-core" % "2.1.0"
  val uimaTools = "org.apache.uima" % "uimaj-tools" % "2.6.0"

  // Testing
  val specs2 = "org.specs2" %% "specs2" % "2.3.12" % "test"
}
