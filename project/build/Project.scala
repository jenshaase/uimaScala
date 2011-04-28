/*
 * Copyright (C) 2011 by Jens Haase <je.haase@googlemail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import jenshaase.uimaScala.descriptor.UimaTypeSystem
import sbt._
import jenshaase.uimaScala.sbt.UimaScalaPlugin

class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject with UimaScalaPlugin {

  override def managedStyle = ManagedStyle.Maven
  lazy val publishTo = Resolver.file("GitHub Pages", new java.io.File("../../jenshaase.github.com/maven/"))

  // Projects
  val core = project("uima-core", "uima-core", new UimaCoreProject(_) with IdeaProject)
  val toolkit = project("uima-toolkit", "uima-toolkit", new UimaToolkitProject(_) with IdeaProject, core)
  val examples = project("uima-examples", "uima-examples", new UimaExamplesProject(_) with IdeaProject, toolkit)
  
  object Dependencies {
    lazy val uimaFit = "org.uimafit" % "uimafit" % "1.1.0"
    
    lazy val specs = "org.specs2" %% "specs2" % "1.1" % "test"
  }
  
  object Repositories {
    lazy val uima = "UIMA" at "http://people.apache.org/repo/m2-incubating-repository"
    
    val scalaToolsSnapshots = "snapshots" at "http://scala-tools.org/repo-snapshots"
    val scalaToolsReleases  = "releases" at "http://scala-tools.org/repo-releases"
  }
  
  class UimaCoreProject(info: ProjectInfo) extends DefaultProject(info) {
    val uima = Repositories.uima
    val scalaToolsSnapshots = Repositories.scalaToolsSnapshots
    val scalaToolsReleases = Repositories.scalaToolsReleases
    
    val uimaFit = Dependencies.uimaFit
    val specs = Dependencies.specs
    
    def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
    override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
  }
  
  class UimaToolkitProject(info: ProjectInfo) extends DefaultProject(info) with UimaScalaPlugin {
    
    def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
    override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
    
    val toolkitTypSystem = UimaTypeSystem("uimaScalaToolkit")(
      _.description("Contains all type system descriptor for this toolkit"),

      _.withType("jenshaase.uimaScala.toolkit.types.Token", "uima.tcas.Annotation")(
        _.description("A simple token annotation")
      ),

      _.withType("jenshaase.uimaScala.toolkit.types.Sentence", "uima.tcas.Annotation")(
        _.description("A simple sentence annotation")
      ),

      _.withType("jenshaase.uimaScala.toolkit.types.Stopword", "uima.tcas.Annotation")(
        _.description("A stopword annotation")
      )
    )
  }
  
  class UimaExamplesProject(info: ProjectInfo) extends DefaultProject(info) with UimaScalaPlugin {
    
  }
}
