import com.github.jenshaase.uimascala.UimaSbtPlugin._

//import sbtrelease.ReleasePlugin._

lazy val commonSettings = Seq(
  organization := "com.github.jenshaase.uimascala",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).
  aggregate(core, toolkit)

lazy val core = (project in file("uima-core")).
  settings(commonSettings: _*).
  settings(releaseSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.apache.uima" % "uimafit-core" % "2.2.0",
      "org.scalaz.stream" %% "scalaz-stream" % "0.7a",
      "org.specs2" %% "specs2-core" % "3.8.4" % "test"
    )
  )

lazy val toolkit = (project in file("uima-toolkit")).
  settings(commonSettings: _*).
  settings(uimaScalaSettings: _*).
  settings(releaseSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.8.4" % "test"
    )
  ).
  dependsOn(core)


lazy val releaseSettings = Seq(
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if ( version.value.trim.endsWith( "SNAPSHOT" ) )
      Some( "snapshots" at nexus + "content/repositories/snapshots" )
    else
      Some( "releases"  at nexus + "service/local/staging/deploy/maven2" )
  },
  publishMavenStyle := true,
  pomExtra := (
    <scm>
      <url>git@github.com:jenshaase/uimascala.git</url>
      <connection>scm:git:git@github.com:jenshaase/uimascala.git</connection>
    </scm>
    <developers>
      <developer>
        <id>jenshaase</id>
        <name>Jens Haase</name>
      </developer>
    </developers>
  )
)
