/**
 * Copyright (C) 2011 Jens Haase
 */
package uimascala

import sbt._
import Keys._

object Publish {
	final val Snapshot = "-SNAPSHOT"
	
	lazy val settings = Seq(
		publishTo	:= uimascalaPublishTo
	)

	lazy val versionSettings = Seq(
    	commands += stampVersion
  	)

	def uimascalaPublishTo =
		Some(Resolver.file("Local", Path.userHome / "programming" / "jenshaase.github.com" / "maven" asFile)(Patterns(true, Resolver.mavenStyleBasePattern)))

  def stampVersion = Command.command("stamp-version") { state =>
    append((version in ThisBuild ~= stamp) :: Nil, state)
  }

  // TODO: replace with extracted.append when updated to sbt 0.10.1
  def append(settings: Seq[Setting[_]], state: State): State = {
    val extracted = Project.extract(state)
    import extracted._
    val append = Load.transformSettings(Load.projectScope(currentRef), currentRef.build, rootProject, settings)
    val newStructure = Load.reapply(session.original ++ append, structure)
    Project.setProject(session, newStructure, state)
  }

  def stamp(version: String): String = {
    if (version endsWith Snapshot) (version stripSuffix Snapshot) + "-" + timestamp(System.currentTimeMillis)
    else version
  }

  def timestamp(time: Long): String = {
    val format = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss")
    format.format(new java.util.Date(time))
  }
}