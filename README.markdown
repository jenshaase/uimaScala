# UimaScala [![Build Status](https://travis-ci.org/jenshaase/uimaScala.svg?branch=master)](https://travis-ci.org/jenshaase/uimaScala)

## About

uimaScala is toolkit to develop natural language application in
Scala. It bases mainly on
[uimaFIT](https://uima.apache.org/uimafit.html), which itsself bases on
[Apache UIMA](http://uima.apache.org/). To develop natural language
processing (NLP) application in [Apache UIMA](http://uima.apache.org/)
you need to work with lots of XML files. For nearly every Java class
you will need an XML File. If your Java class changes you also need to
change you XML file. [uimaFIT](http://code.google.com/p/uimafit/)
tries to solve this problem with reflection and nearly removes all XML
files.

This project started as a wrapper for
[uimaFIT](https://uima.apache.org/uimafit.html). With Scala's collection
library and the functional programming stuff it is a lot easier to
develop NLP Application. Also a type safe configuration system and a
nicer DSL was added.

This readme provides a short introduction. More documentation will be
added later.

## Setup a project

To use this project add following configuration to your `built.sbt`
file. Uimscala requires Scala version `2.11`

~~~
scalaVersion := "2.11.1"

resolvers ++= Seq(
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies += "com.github.jenshaase.uimascala" %% "uimascala-core" % "0.5.0-SNAPSHOT"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)
~~~

Next you need to tell UIMA where to find the description
files. Therefore add the file `types.txt` to the folder
`src/main/resources/META-INF/org.apache.uima.fit`. Add following
content:

~~~
classpath*:desc/types/**/*.xml
~~~

## A simple annotator

Annotators in UIMA will process a document. Most of the time they are
using annotations from previous annotators and combine them to new
annotations. The following annotator is Tokenizer. It looks at the
text and identifies single words, also called tokens. We can use
Java's `BreakIterator` to tokenize the text. You will find the class
also in the toolkit with some additional processing:

~~~
package com.github.jenshaase.test

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import java.util.Locale
import org.apache.uima.jcas.JCas
import java.text.BreakIterator

class BreakIteratorTokenizer extends SCasAnnotator_ImplBase {

  object locale extends Parameter[Locale](Locale.getDefault)

  def process(jcas: JCas) = {
    val bi = BreakIterator.getWordInstance(locale.is)
    bi.setText(jcas.getDocumentText)

    var last = bi.first
    var cur = bi.next
    while (cur != BreakIterator.DONE) {
      if (jcas.getDocumentText().substring(last, cur).trim != "") {
        jcas.annotate[Token](last, cur)
      }

      last = cur
      cur = bi.next
    }
  }
}
~~~

An annotator in uimaScala extends the `SCasAnnotator_ImplBase`
class. To implement this class you need to implement the `process`
method. Here we use Java's `BreakIterator` to process the
document. For each token we add a new `Token` type (the next part will
explain how to create such type). You can also see the `locale`
configuration parameter. It has a name (`locale`) and type (`Locale`)
and a default value `Locale.getDefault`. These parameter can be change
when using this component in a UIMA pipeline.


## Adding your own type system description

The goal of an annotator is to add new annotation to text. With UIMA
you can create you custom annotation with XML Files and then generate
the Java classes. uimaScala uses a Scala marco and custom DSL to
provide this features. In order to create your type system you need to
define an object in your scala code:

~~~
package com.github.jenshaase.test

import com.github.jenshaase.uimascala.core.description._ 

@TypeSystemDescription
object TypeSystem {

  val Token = Annotation {
    val pos = Feature[String]
    val lemma = Feature[String]
    val stem = Feature[String]
  }

  val Sentence = Annotation {}
}
~~~

After running `compile` your can see following output on your sbt console:

~~~
Jul 03, 2014 8:28:37 AM org.apache.uima.tools.jcasgen.UimaLoggerProgressMonitor subTask(35)
INFORMATION:  >>JCasGen Creating: 'com.github.jenshaase.test.Token'.
Jul 03, 2014 8:28:37 AM org.apache.uima.tools.jcasgen.UimaLoggerProgressMonitor subTask(35)
INFORMATION:  >>JCasGen Creating: 'com.github.jenshaase.test.Token_Type'.
Jul 03, 2014 8:28:37 AM org.apache.uima.tools.jcasgen.UimaLoggerProgressMonitor subTask(35)
INFORMATION:  >>JCasGen Creating: 'com.github.jenshaase.test.Sentence'.
Jul 03, 2014 8:28:37 AM org.apache.uima.tools.jcasgen.UimaLoggerProgressMonitor subTask(35)
INFORMATION:  >>JCasGen Creating: 'com.github.jenshaase.test.Sentence_Type'
~~~

Now the necessary Java files are created. You need to run `compile`
again to compile the generated Java sources.

## Running a pipeline

Tu run a pipeline uimascala use
[scalaz-stream](https://github.com/scalaz/scalaz-stream) library. To
run a pipeline we need to convert documents to a CAS and process the
CAS with our annotators:

~~~
package com.github.jenshaase.test

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.stream._
import scalaz._, Scalaz._
import scalaz.stream._
import java.util.Locale

object Main extends App {

  val p = Process("this is a text", "and another text") |>
    casFromText |>
    annotate(new BreakIteratorTokenizer().config(_.locale := Locale.US)) |>
    extractCas { cas =>
      cas.select[Token].map(_.getCoveredText).toList
    }

  println(p.toList)

  p.toList == List(
    List("this", "is", "a", "text"),
    List("and", "another", "text")
  )
}

~~~


## TODO

* Add more documentation
