/**
 * Copyright (C) 2011 Jens Haase
 */
package uimascala

import sbt._
import Build.data
import compiler.Discovery
import complete._
import DefaultParsers._
import classpath._
import std.TaskExtra._
import Project.Initialize

import sbinary.DefaultProtocol.StringFormat
import Cache.seqFormat

import Types._
import Path._
import Keys._

import xml.{ XML, Node, PrettyPrinter }

import org.apache.uima.UIMAFramework
import org.apache.uima.util.{ CasCreationUtils, XMLInputSource }
import org.apache.uima.cas.impl.CASImpl
import org.apache.uima.tools.jcasgen._
import java.io.FileWriter

object SbtUimaKeys {
  val Uima = config("uima")

  // Settings
  val typeSystem = SettingKey[Seq[UimaTypeSystem]]("type-system")
  val typeSystemDescriptorPath = SettingKey[File]("type-system-descriptor-path")
  val xmlDescriptorPath = SettingKey[File]("xml-descriptor-path")

  // Task
  val definedXmlDescriptor = TaskKey[Seq[String]]("defined-xml-descriptor")
  val genXmlDescriptor = InputKey[Unit]("generate-xml-descriptor")
  val jcasgen = TaskKey[Unit]("jcasgen", "Starts the jcasgen gui")
}

object SbtUimaPlugin extends Plugin with BuildCommon {

  import SbtUimaKeys._

  lazy val uimaSettings = inConfig(Uima)(Seq(
    // Settings
    typeSystem := Seq(),
    typeSystemDescriptorPath <<= (resourceDirectory in Runtime) { (dir) ⇒ dir / "desc" / "types" },
    xmlDescriptorPath <<= (resourceDirectory in Runtime) { (dir) ⇒ dir / "desc" },

    // Tasks
    definedXmlDescriptor <<= TaskData.write((compile in Runtime) map discoverXmlDescriptors) triggeredBy (compile in Runtime),
    genXmlDescriptor <<= genXmlDescriptorTask,
    libraryDependencies += "org.apache.uima" % "uimaj-tools" % "2.3.1",
    jcasgen <<= run("org.apache.uima.tools.jcasgen.Jg"), // TODO: Add arguments: First arg: <xmlDescriptorPath> <xmlDescriptorPath>

    // Commands
    commands += uimaTypeSystemCommand))

  /**
   * Runs a main method. This is used to start uima guis
   */
  def run(mainClass: String, args: Seq[String] = Seq.empty): Initialize[Task[Unit]] =
    (fullClasspath in Runtime, runner, streams) map {
      case (cp, runner, s) ⇒ runner.run(mainClass, data(cp), args, s.log)
    }

  /**
   * Discovers all class that are subclasses of
   *   jenshaase.uimaScala.core.XmlDescriptor
   */
  def discoverXmlDescriptors(analysis: inc.Analysis): Seq[String] = {
    val descClass = "jenshaase.uimaScala.core.XmlDescriptor"

    val discovery = Discovery(Set(descClass), Set.empty)(Tests allDefs analysis)
    discovery collect { case (df, disc) if (disc.baseClasses contains descClass) ⇒ df.name } toSeq
  }

  /**
   * This task generate the analysis engine xml description.
   * As argument of the task add the classes for which the xml description should be rendered (multiple possible)
   * If empty all descriptions will be created
   */
  def genXmlDescriptorTask =
    InputTask(TaskData(definedXmlDescriptor)(genXmlDescriptorParser)(Nil)) { result ⇒
      (fullClasspath in Runtime, scalaInstance, xmlDescriptorPath, streams, result, javaSource in Runtime) map {
        case (cp, inst, path, s, descClasses, javaPath) ⇒
          val loader = ClasspathUtilities.makeLoader(data(cp), inst)
          val formatter = new PrettyPrinter(80, 2)
          try {
            descClasses.foreach { descClass ⇒
              val desc = Class.forName(descClass, true, loader).newInstance.asInstanceOf[{ def toXml: Node; def xmlType: String }];

              val filename = path / desc.xmlType / descClass + ".xml"
              writeXml(filename, desc.toXml)
              s.log("Successful created descriptor: " + filename)

              // Generate java classes for typ system
              if (desc.xmlType == "types") {
                val xmlIS = new XMLInputSource(filename)
                val tsd = UIMAFramework.getXMLParser.parseTypeSystemDescription(xmlIS)
                val cas = CasCreationUtils.createCas(tsd, null, null)

                val jg = new Jg()
                jg.mainForCde(null, new UimaLoggerProgressMonitor(), new LogThrowErrorImpl(), filename, javaPath.toString, tsd.getTypes, cas.asInstanceOf[CASImpl])
              }
            }
          } catch {
            case e: ClassNotFoundException ⇒
          }
          print("")
      }
    }

  /**
   * The parser for the xml descriptor class.
   * This enabled tab completion in sbt
   */
  def genXmlDescriptorParser: (State, Seq[String]) ⇒ Parser[Seq[String]] = { (state, descClasses) ⇒
    (Space ~> token(NotSpace examples descClasses.toSet)).* map (l ⇒ if (l.isEmpty) descClasses else l)
  }

  private val pp = new PrettyPrinter(500, 2)
  def writeXml(file: String, n: Node) = {
    new File(file).getParentFile.mkdirs
    val sb = new StringBuilder()

    pp.format(n, sb)

    val fw = new FileWriter(file)
    fw.write("<?xml version='1.0' encoding='UTF-8'?>\n")
    fw.write(sb.toString)
    fw.write("\n")
    fw.close()
  }

  val uimaTypeSystemCommand = Command.command("uima-type-system") { state ⇒
    val extracted = Project.extract(state)
    val structure = extracted.structure

    def setting[A](
      key: SettingKey[A],
      configuration: Configuration = Configurations.Compile) = {
      key in (extracted.currentRef, configuration) get structure.data
    }

    val typeSystems = setting(typeSystem).getOrElse(Seq())
    val descriptorPath = setting(typeSystemDescriptorPath).get
    val javaPath = setting(javaSource).get

    typeSystems.foreach(s ⇒ {
      // Create xml
      val filename = descriptorPath / s.name + ".xml"
      new File(filename).getParentFile.mkdirs
      XML.save(filename, s.get, "UTF-8", true, null)

      // Generate java code
      val xmlIS = new XMLInputSource(filename)
      val tsd = UIMAFramework.getXMLParser.parseTypeSystemDescription(xmlIS)
      val cas = CasCreationUtils.createCas(tsd, null, null)

      val jg = new Jg()
      jg.mainForCde(null, new UimaLoggerProgressMonitor(), new LogThrowErrorImpl(), filename, javaPath.toString, tsd.getTypes, cas.asInstanceOf[CASImpl])
    });

    state
  }
}