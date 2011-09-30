/**
 * Copyright (C) 2011 Jens Haase
 */
package uimascala

import sbt._
import Keys._
import Defaults._
import Scope.GlobalScope
import sbt.CommandSupport.logger

import xml.XML

import org.apache.uima.UIMAFramework
import org.apache.uima.util.{CasCreationUtils, XMLInputSource}
import org.apache.uima.cas.impl.CASImpl
import org.apache.uima.tools.jcasgen._

object SbtUimaPlugin extends Plugin {

  val typeSystem = SettingKey[Seq[UimaTypeSystem]]("type-system")
  val typeSystemDescriptorPath = SettingKey[File]("type-system-descriptor-path")

  val uimaSettings = Seq(
    typeSystem := Seq(),
    typeSystemDescriptorPath <<= (resourceDirectory in Runtime){ (dir) => dir / "desc" / "types" },
    commands += uimaTypeSystemCommand,
    libraryDependencies += "org.apache.uima" % "uimaj-tools" % "2.3.1"
  )

  val uimaTypeSystemCommand = Command.command("uima-type-system"){ state =>
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

    typeSystems.foreach(s => {
        // Create xml
        val filename = descriptorPath / s.name+".xml"
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

