package com.github.jenshaase.uimascala

import sbt._
import Keys._
import plugins._

import org.apache.uima.UIMAFramework
import org.apache.uima.util.{ CasCreationUtils, XMLInputSource }
import org.apache.uima.cas.impl.CASImpl
import org.apache.uima.tools.jcasgen._

object UimaSbtPlugin extends Plugin {

  val uimaConfig = config("uima")

  val jcasGen = TaskKey[Unit]("jcasgen")
  val visualDebugger = TaskKey[Unit]("visualDebugger")

  def uimaScalaSettings = Seq(
    sourceDirectory in uimaConfig <<= (resourceDirectory in Compile) { _ / "desc" / "types" },
    javaSource in uimaConfig <<= (sourceManaged in Compile) { _ / "java" },
    sourceGenerators in Compile <+= generateTypeSystemSourcesTask,
    managedSourceDirectories in Compile <+= (javaSource in uimaConfig),
    cleanFiles <+= (javaSource in uimaConfig),
    jcasGen <<= jcasGenTask,
    visualDebugger <<= visualDebuggerTask
  )

  def generateTypeSystemSourcesTask =
    (sourceDirectory in uimaConfig, javaSource in uimaConfig) map { (srcDir, targetDir) =>
      generateTypeSystemSources(srcDir, targetDir)
    }

  def generateTypeSystemSources(srcDir: File, targetDir: File): Seq[File] = {
    (srcDir ** "*.xml").get foreach { filename =>
      val xmlIS = new XMLInputSource(filename)
      val tsd = UIMAFramework.getXMLParser.parseTypeSystemDescription(xmlIS)
      val cas = CasCreationUtils.createCas(tsd, null, null)
      val jg = new Jg()
      jg.mainGenerateAllTypesFromTemplates(
        null, new UimaLoggerProgressMonitor(), new LogThrowErrorImpl(),
        filename.getAbsolutePath, targetDir.getAbsolutePath, tsd.getTypes,
        cas.asInstanceOf[CASImpl], classOf[UimaScalaTypeTemplate],
        classOf[UimaScala_TypeTemplate], "", false, null
      )
    }

    (targetDir ** "*.java").get
  }

  def jcasGenTask =
    (streams) map { streams =>
      Run.executeTrapExit(
        (new Jg()).main0(Array[String](), null, null, new LogThrowErrorImpl()),
        streams.log
      )
      ()
    }

  def visualDebuggerTask =
    (streams) map { streams =>
      Run.executeTrapExit (
        org.apache.uima.tools.cvd.CVD.main(Array[String]()),
        streams.log
      )
      ()
    }
    
}
