import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {

  // Testing
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.7" % "test"
}
