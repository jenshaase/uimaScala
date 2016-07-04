sbtPlugin := true

organization := "com.github.jenshaase.uimascala"

libraryDependencies ++= Seq(
  "org.apache.uima" % "uimaj-tools" % "2.8.1"
)

releasePublishArtifactsAction := PgpKeys.publishSigned.value

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if ( version.value.trim.endsWith( "SNAPSHOT" ) )
    Some( "snapshots" at nexus + "content/repositories/snapshots" )
  else
    Some( "releases"  at nexus + "service/local/staging/deploy/maven2" )
}

publishMavenStyle := true

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

