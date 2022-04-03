import play.api.libs.json.{JsString, JsValue, Json}

import java.nio.file.{Files, Paths}
import scala.io.{BufferedSource, Source}
import scala.io.StdIn.readLine

trait Task {
  def doTask(autoFix: Boolean, skipFix: Boolean): Unit
}

object Task {
  def create(location: String): Task = {
    val configPath = Paths.get(location, "config.json")
    if (Files.exists(configPath)) {
      new ScriptTask(location)
    } else {
      EmptyTask(location)
    }
  }
}

class ScriptTask(location: String) extends Task {
  private val configSource: BufferedSource = Source.fromFile(Paths.get(location, "config.json").toFile)
  private val config: JsValue = Json.parse(try configSource.mkString finally configSource.close())

  def doTask(autoFix: Boolean, skipFix: Boolean): Unit = {
    val analyzeFile = config \ "analyze"
    val solutionFile = config \ "solution"
    val description = (config \ "description")
      .getOrElse(JsString(location))

    if (analyzeFile.isEmpty) {
      println(s"Cannot perform task $location: analyze file is not specified. Skipping...")
      return
    }

    val analyzeProcess = new ProcessBuilder(Paths.get(location, analyzeFile.as[String]).toString)
      .start()

    if (analyzeProcess.waitFor() != 0) {
      println(Console.RED + "[FAIL] " + Console.WHITE + description.as[String])

      // Check if all conditions are met before running the solution file. The command must:
      // 1: not be run with --scan-only
      // 2: contain an executable file called solution.sh
      // 3: either be used with --auto-fix or user answered yes on prompt
      if (!skipFix && !solutionFile.isEmpty && (autoFix || promptFix())) {
        println("Attempting to fix issue...")
        val fixProcess = new ProcessBuilder(Paths.get(location, solutionFile.as[String]).toString)
          .start()
        if (fixProcess.waitFor() != 0) {
          println(Console.MAGENTA + "Solution file exited with non-zero status. You may need to fix this issue another way." + Console.WHITE)
        } else {
          println("Solution file exited successfully. You may want to run canary again to verify the fix.")
        }
      }
    } else {
      println(Console.GREEN + "[PASS] " + Console.WHITE + description.as[String])
    }
  }

  private def promptFix(): Boolean = {
    while (true) {
      // Prompt user until they enter a y or n
      println("Attempt to fix this issue? [y/n]")
      val input = readLine()
      if (input.nonEmpty) {
        if (input.equals("n") || input.equals("N")) {
          return false
        } else if (input.equals("y") || input.equals("Y")) {
          return true
        }
      }
    }
    false
  }
}

// A task will require at minimum an analyze file and a description. If these can't be resolved then
// an empty task is created to alert the user that the task is invalid and will not run.
case class EmptyTask(location: String) extends Task {
  override def doTask(autoFix: Boolean, skipFix: Boolean): Unit = {
    println(
      s"""
        |Unable to initialize task at $location. If this is a package, please contact the package's maintainers.
        |If this is a custom scan, make sure an analyze.sh and description.txt file exist in the directory for this task.
        |""".stripMargin)
  }
}