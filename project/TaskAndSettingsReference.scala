import sbt._
import Keys._

object TaskAndSettingsReference extends Plugin {
  val grepSettings = taskKey[File]("generate a summary of tasks and settings in an sbt plugin project")

  private val Format = """\s*val\s+(\w+)\s*=\s*(task|setting)Key\s*\[\s*(\S+)\s*\]\s*\(\s*"([^"]+)".*""".r

  override def projectSettings = Seq(
    grepSettings := {

      val heading ="# Soapbox Task and Setting Reference\n\n"

      val ls = for {
        src <- (sources in Compile).value
        Format(name, kind, dt, descr) <- IO.readLines(src)
      }
      yield s"""## $name
               |
               |${kind.capitalize} type:
               |:    `$dt` 
               |
               |
               |Description:
               |:    $descr
               |
               |""".stripMargin

      val output = baseDirectory.value / "TaskReference.md"

      IO.write(output, (heading +: ls.mkString).mkString)
      output
    }
  )
}
