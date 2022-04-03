import Canary.fatal

import java.nio.file.Paths
import scala.collection.mutable.ListBuffer

case class CheckCommand(packages: List[String], directory: Option[String], localPath: String, autoFix: Boolean, scanOnly: Boolean) extends Command {
  override def run(): Unit = {
//    val tasks: ListBuffer[Task] = ListBuffer()
//
//    if (packages.isEmpty && directory.isEmpty)
//      fatal("Must provide one of directory or packages")
//
//    // Gather all tasks from the directory if one was specified
//    if (directory.nonEmpty) {
//      val dir = directory.get
//      tasks.addAll(
//        TaskBuilder.fromDir(dir)
//          .getOrElse({
//            fatal("Fatal: " + dir + " is not a directory.")
//          })
//      )
//    }
//
//    // Iterate over package list and add all tasks from the package
//    for (packageName <- packages) {
//      tasks.addAll(
//        TaskBuilder.fromDir(Paths.get(localPath, packageName).toString)
//          .getOrElse({
//            fatal("Fatal: " + packageName + " was not found. Please check your package install location or install it with 'canary install " + packageName + "'")
//          })
//      )
//    }
//
//    // Analyze each issue and fix
//    for (task <- tasks) {
//      task.doTask(autoFix, scanOnly)
//    }
    println(packages)
    println(directory)
    println(localPath)
    println(autoFix)
    println(scanOnly)
  }
}

object CheckCommand {
  val usage: String =
    """
      |Usage: canary check [OPTIONS] [PACKAGES...]
      |
      |Checks the system against rules defined in the specified packages
      |
      |Options:
      |    --local     Path to local canary packages. Default location is ~/.canary
      |    --directory Custom package to use to scan system
      |    --auto-fix  Fix issues found without prompting
      |    --scan-only Scan for issues only and don't prompt to fix
      |""".stripMargin
}
