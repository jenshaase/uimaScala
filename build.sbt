import com.github.jenshaase.uimascala.UimaSbtPlugin._

lazy val commonSettings = Seq(
  organization := "com.github.jenshaase.uimascala",
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    "org.specs2" %% "specs2-core" % "3.8.4" % "test"
  )
)

lazy val componentSettings = commonSettings ++ releaseSettings

lazy val root = (project in file(".")).
  settings(
    publishArtifact in Compile := false
  ).
  aggregate(
    core, typeSystem,
    breakIteratorSegmenter, regexTokenizer, whitespaceTokenizer, stanfordSegmenter,
    stanfordPosTagger
  )

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(releaseSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.apache.uima" % "uimafit-core" % "2.2.0",
      "org.scalaz.stream" %% "scalaz-stream" % "0.8"
    )
  )

lazy val typeSystem = (project in file("type-system")).
  settings(componentSettings: _*).
  settings(uimaScalaSettings: _*).
  dependsOn(core)

lazy val breakIteratorSegmenter = (project in file("segmenter/break-iterator-segmenter")).
  settings(componentSettings).
  dependsOn(core, typeSystem)

lazy val regexTokenizer = (project in file("segmenter/regex-tokenizer")).
  settings(componentSettings).
  dependsOn(core, typeSystem)

lazy val whitespaceTokenizer = (project in file("segmenter/whitespace-tokenizer")).
  settings(componentSettings).
  dependsOn(core, typeSystem, regexTokenizer)

lazy val stanfordSegmenter = (project in file("segmenter/stanford-segmenter")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0"
    )
  ).
  dependsOn(core, typeSystem)

lazy val stanfordPosTagger = (project in file("part-of-speech-tagger/stanford-pos-tagger")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" % "test" classifier "models-german"
    )
  ).
  dependsOn(core, typeSystem)

lazy val toolkit = (project in file("toolkit")).
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
