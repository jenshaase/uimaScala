import sbt._
import Keys._

object Release extends Build {

    lazy val prerelease = TaskKey[Unit]("prerelease")
    /*lazy val fullRelease = TaskKey[Unit]("full-release")
    lazy val pushMain = TaskKey[Unit]("push-main")
    lazy val myVersion = TaskKey[Unit]("my-version")*/

    def releaseSettings: Seq[Setting[_]] = fullReleaseSettings
        
    def fullReleaseSettings: Seq[Setting[_]] = Seq(
        prerelease := println(Prerelease),
        commands += fullReleaseCmd
    )
    
    def fullReleaseCmd = Command.command("full-release") { (state: State) =>
        val extracted = Project.extract(state)
        import extracted._
        val transformed = session.mergeSettings map ( s => updateVersion(s) )
        val newStructure = Load.reapply(transformed, structure)
        val newState = Project.setProject(session, newStructure, state)
        
        if (!gitIsCleanWorkingTree) {
            println("Cannot publish release. Working directory is not clean.");
            state.fail
        } else {
            val correctDep = (libraryDependencies in currentRef get structure.data).map{
                _.forall(lib => !lib.revision.contains("SNAPSHOT"))
            }.getOrElse(true)
            
            if (correctDep) {
                tag(state)
            } else {
                println("Cannot publish a release with snapshotted dependencies.");
                state.fail
            }
        }
    }
    
    def tag(state: State) = {
        val extracted = Project.extract(state)
        import extracted._
    
        val v = (version in currentRef get structure.data).get
        commitAndPush(v, tag = Some("v"+v))
        
        state
    }
    
    def updateVersion(s: Setting[_]): Setting[_] = s.key.key match {
        case Keys.version.key =>
            s.asInstanceOf[Setting[String]].mapInit( (key, value) =>
                Version.fromString(value) match {
                    case Right(r: BasicVersion) => r.withExtra(None).toString
                    case _ => value
                }
            )
        case _ => s
    }
    
    def commitAndPush(msg: String, tag: Option[String] = None)
    {
        git("add", ".")
        git("commit", "-m", msg, "--allow-empty")
        for(tagString <- tag) git("tag", tagString)
        git("push", "--tags", "-n")
    }
    
    def git(args: String*): Unit =
    {
       val full = "git" +: args
       println(full)
       // full !
    }
    
    def gitIsCleanWorkingTree =
        ("git status" !!).contains("nothing to commit (working directory clean)")
    
    def Prerelease = """
Before running full-release:
1. Ensure all code is committed and the working directory is completely clean. 'git status' should show no untracked files.
2. 'test'
3. Set the release version in sREADME
"""
}

sealed trait Version
case class BasicVersion(major: Int, minor: Option[Int], micro: Option[Int], extra: Option[String]) extends Version
{
	import Version._
	require(major >= 0, "Major revision must be nonnegative.")
	require(minor.isDefined || micro.isEmpty, "Cannot define micro revision without defining minor revision.")
	requirePositive(minor)
	requirePositive(micro)
	require(isValidExtra(extra))
	
	def incrementMicro = BasicVersion(major, minor orElse Some(0), increment(micro), extra)
	def incrementMinor = BasicVersion(major, increment(minor), micro, extra)
	def incrementMajor = BasicVersion(major+1, minor, micro, extra)
	def withExtra(newExtra: Option[String]) = BasicVersion(major, minor, micro, newExtra)
	
	override def toString = major +
		minor.map(minorI => "." + minorI + micro.map(microI => "." + microI).getOrElse("")).getOrElse("") +
			extra.map(x => "-" + x).getOrElse("")
}
case class OpaqueVersion(value: String) extends Version
{
	require(!value.trim.isEmpty)
	override def toString = value
}
object Version
{
	def increment(i: Option[Int]) = Some(i.getOrElse(0) + 1)
	def requirePositive(i: Option[Int]) { i.foreach(x => require(x >= 0)) }
	
	import java.util.regex.Pattern
	val versionPattern = Pattern.compile("""(\d+)(?:\.(\d+)(?:\.(\d+))?)?(?:-(.+))?""")
	def fromString(v: String): Either[String, Version] =
	{
		val trimmed = v.trim
		if(trimmed.isEmpty)
			Left("Version cannot be empty.")
		else
		{
			val matcher = versionPattern.matcher(trimmed)
			import matcher._
			if(matches)
			{
				def toOption(index: Int) =
				{
					val v = group(index)
					if(v == null) None else Some(v)
				}
				def toInt(index: Int) = toOption(index).map(_.toInt)
				val extra = toOption(4)
				if(isValidExtra(extra))
					Right(BasicVersion(group(1).toInt, toInt(2), toInt(3), extra))
				else
					Right(OpaqueVersion(trimmed))
			}
			else
				Right(OpaqueVersion(trimmed))
		}
	}
	def isValidExtra(e: Option[String]): Boolean = e.map(isValidExtra).getOrElse(true)
	def isValidExtra(s: String): Boolean = !(s.trim.isEmpty || s.exists(java.lang.Character.isISOControl))
}

