# UimaScala

## About
uimaScala is toolkit to develop natural language application in Scala. It bases mainly on [uimaFIT](http://code.google.com/p/uimafit/), which itsself bases on [Apache UIMA](http://uima.apache.org/). To develop natural language processing (NLP) application in [Apache UIMA](http://uima.apache.org/) you need to work with lots of XML files. For nearly every Java class you will need an XML File. If your Java class changes you also need to change you XML file. [uimaFIT](http://code.google.com/p/uimafit/) tries to solve this problem with reflection and nearly removes all XML files.

This project started as a wrapper for [uimaFIT](http://code.google.com/p/uimafit/). With Scala's collection library and the functional programming stuff it is a lot easier to develop NLP Application. Also a type safe configuration system and a nicer DSL was added.

This readme provides a short introduction. More documentation will be added later.

## Setup a project

1. Create a new [sbt](https://github.com/harrah/xsbt/wiki) project
2. Add the uimaScala sbt plugin to the project. Create or edit `project/plugins.sbt` and add:
    
    `resolvers += "uimaScala plugin repo" at "http://jenshaase.github.com/maven"`

    `addSbtPlugin("com.github.jenshaase.uimascala" % "uimascala-sbt-plugin" % "0.3.1")`

3. Next, add this project to your `built.sbt`. You can use just the core component or the toolkit. The toolkit provides some basic components to develop Natural Language Applications. If you just want to use the core library add:

    `resolvers += "uimaScala plugin repo" at "http://jenshaase.github.com/maven"`

    `libraryDependencies += "com.github.jenshaase.uimascala" %% "uimascala-core" % "0.3.0"`

     for the toolkit use these two lines:

    `resolvers += "uimaScala plugin repo" at "http://jenshaase.github.com/maven"`

    `libraryDependencies += "com.github.jenshaase.uimascala" %% "uimascala-core" % "0.3.0"`

4. Now you are able to start to develop your application

## A simple annotator

Annotators in UIMA will process a document. Most of the time they are using annotations from previous annotators and combine them to new annotations. The following annotator is Tokenizer. It looks at the text and identifies single words, also called tokens. We can use Java's `BreakIterator` to tokenize the text. You will find the class also in the toolkit with some additional processing:


	package com.example.annotator

	// some imports ...

	class BreakIteratorTokenizer extends SCasAnnotator_ImplBase {

	  object locale extends Parameter[Locale](Locale.getDefault)

	  def process(jcas: JCas) = {
	    val bi = BreakIterator.getWordInstance(locale.is)
	    bi.setText(jcas.getDocumentText)

	    var last = bi.first
	    var cur = bi.next
	    while (cur != BreakIterator.DONE) {
	      val token = new Token(jcas, last, cur)
	      token.addToIndexes()

	      last = cur
	      cur = bi.next
	    }
	  }
	}

An annotator in uimaScala extends the `SCasAnnotator_ImplBase` class. To implement this class you need to implement the `process` method. Here we use Java's `BreakIterator` to process the document. For each token we add a new `Token` type (the next part will explain how to create such type). You can also see the `locale` configuration parameter. It has a name (`locale`) and type (`Locale`) and a default value `Locale.getDefault`. These parameter can be change when using this component in a UIMA pipeline.


## Adding your own type system description

The goal of an annotator is to add new annotation to text. With UIMA you can create you custom annotation with XML Files and then generate the Java classes. uimaScala uses a SBT plugin and custom Scala DSL to provide this features. First add the sbt plugin
to your project, as described in the setup. Then create a new class in your project that extends `TypeSystemDescription`. In this class you can add your custom types. Here an example:

		package com.example.description

		import com.github.jenshaase.uimascala.core.description._
		import UimaTyp._

		class MyDescription extends TypeSystemDescription {

		  def name = "MyDescription"

		  def basePackage = "com.example.types"

		  def types = Seq(
		    "Sentence" extend UimaAnnotation features(),
		    "Token" extend UimaAnnotation features(StringFeature("PosTag"), StringFeature("Lemma"))
		  )
		}

Now from from the sbt console execute `uima:generate-xml-descriptor`. This will generate four Java classes in `src/main/java`.

## Running a pipeline

To run a pipeline we need to read documents from a source, process these documents and write the extracted information to some destination. In the following example we create a simple pipeline that just reads files from a directory and than annotates tokens:

	package com.example

	// some imports ...

	object Main extends App {
		
		new TextFileReader().config(_.path := new File("some/path")) ~>
			new BreakIteratorTokenizer().config(_.config := Locale.GERMAN) run()

	}

## TODO

* Add more documentation