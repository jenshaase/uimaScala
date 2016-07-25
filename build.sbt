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
    publishArtifact in Compile := false,
    parallelExecution in Test := false
  ).
  aggregate(
    core, typeSystem,
    breakIteratorSegmenter, regexTokenizer, whitespaceTokenizer, stanfordSegmenter, arkTweetTokenizer, openNlpSegmenter, luceneTokenizer,
    stanfordPosTagger, arkTweetPosTagger,
    stanfordParser,
    stanfordNer,
    nGramLanguageIdentifier
    // Do not run these test in build environment because of too much memory consumption
    //mateLemmatizer, mateParser, matePosTagger
  )

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(releaseSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.apache.uima" % "uimafit-core" % "2.2.0",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
      "co.fs2" %% "fs2-core" % "0.9.0-M5"
    )
  )

lazy val typeSystem = (project in file("type-system")).
  settings(componentSettings: _*).
  settings(uimaScalaSettings: _*).
  dependsOn(core)

// ==================================================
// Segmenter

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

lazy val arkTweetTokenizer = (project in file("segmenter/ark-tweet-tokenizer")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "edu.cmu.cs" % "ark-tweet-nlp" % "0.3.2"
    )
  ).
  dependsOn(core, typeSystem)

lazy val openNlpSegmenter = (project in file("segmenter/open-nlp-segmenter")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "org.apache.opennlp" % "opennlp-tools" % "1.6.0",
      "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.opennlp-model-sentence-de-maxent" % "20120616.1" % "test",
      "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.opennlp-model-token-de-maxent" % "20120616.1" % "test"
    ),
    resolvers ++= Seq(
      "ukp-oss-model-releases" at "http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-model-releases-local"
    )
  ).
  dependsOn(core, typeSystem)

lazy val luceneTokenizer = (project in file("segmenter/lucene-tokenizer")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "org.apache.lucene" % "lucene-analyzers-common" % "6.1.0"
    )
  ).
  dependsOn(core, typeSystem)

// ==================================================
// Lemmatizer

lazy val mateLemmatizer = (project in file("lemmatizer/mate-lemmatizer")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "com.googlecode.mate-tools" % "anna" % "3.5",
      "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.matetools-model-lemmatizer-de-tiger" % "20121024.1" % "test"
    ),
    resolvers ++= Seq(
      "ukp-oss-model-releases" at "http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-model-releases-local"
    )
  ).
  dependsOn(core, typeSystem)

// ==================================================
// POS Tagger

lazy val stanfordPosTagger = (project in file("part-of-speech-tagger/stanford-pos-tagger")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" % "test" classifier "models-german"
    )
  ).
  dependsOn(core, typeSystem)

lazy val matePosTagger = (project in file("part-of-speech-tagger/mate-pos-tagger")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "com.googlecode.mate-tools" % "anna" % "3.5",
      "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.matetools-model-tagger-de-tiger" % "20121024.1" % "test"
    ),
    resolvers ++= Seq(
      "ukp-oss-model-releases" at "http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-model-releases-local"
    )
  ).
  dependsOn(core, typeSystem)

lazy val arkTweetPosTagger = (project in file("part-of-speech-tagger/ark-tweet-pos-tagger")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "edu.cmu.cs" % "ark-tweet-nlp" % "0.3.2"
    )
  ).
  dependsOn(core, typeSystem)

// ==================================================
// Parser

lazy val stanfordParser = (project in file("parser/stanford-parser")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" % "test" classifier "models-german"
    )
  ).
  dependsOn(core, typeSystem)

lazy val mateParser = (project in file("parser/mate-parser")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "com.googlecode.mate-tools" % "anna" % "3.5",
      "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.matetools-model-parser-de-tiger" % "20121024.1" % "test"
    ),
    resolvers ++= Seq(
      "ukp-oss-model-releases" at "http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-model-releases-local"
    )
  ).
  dependsOn(core, typeSystem)

// ==================================================
// Name Entity Recognizer

lazy val stanfordNer = (project in file("name-entity-recognizer/stanford-ner")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" % "test" classifier "models-german"
    )
  ).
  dependsOn(core, typeSystem)

// ==================================================
// Language Identifer

lazy val nGramLanguageIdentifier = (project in file("language-identification/n-gram-language-identifier")).
  settings(componentSettings).
  settings(
    libraryDependencies ++= Seq(
      "com.optimaize.languagedetector" % "language-detector" % "0.5"
    )
  ).
  dependsOn(core, typeSystem)


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
