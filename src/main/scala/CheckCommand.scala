import Canary.fatal

import scala.collection.mutable.ListBuffer

/**
 * CheckCommand gathers canary tasks and executes them
 * @param packages  canary packages to analyze
 * @param directory directory to analyze. Optional
 * @param localPath local canary root
 * @param autoFix   indicates issues found by tasks should be fixed without a prompt
 * @param scanOnly  indicates that the user should not be prompted to fix issues. Overrides autoFix
 */
case class CheckCommand(packages: List[String],
                        directory: Option[String],
                        localPath: String,
                        autoFix: Boolean,
                        scanOnly: Boolean) extends Command {

  override def run(): Unit = {
    val tasks: ListBuffer[Task] = ListBuffer()

    if (packages.isEmpty && directory.isEmpty)
      fatal("Must provide one of directory or packages")

    // Gather all tasks from the directory if one was specified
    if (directory.nonEmpty) {
      val dir = directory.get
      tasks.addAll(
        TaskBuilder.fromDir(dir)
          .getOrElse({
            fatal("Fatal: " + dir + " is not a directory.")
          })
      )
    }

    // Iterate over package list and add all tasks from the package
    for (packageName <- packages) {
      tasks.addAll(
        TaskBuilder.fromPackage(localPath, packageName)
          .getOrElse({
            fatal("Fatal: " + packageName + " was not found. Please check your package install location or install it with 'canary install " + packageName + "'")
          })
      )
    }

    // Analyze each issue and fix
    for (task <- tasks) {
      task.doTask(autoFix, scanOnly)
    }
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
      |    --directory Custom package to use to scan system
      |    --auto-fix  Fix issues found without prompting
      |    --scan-only Scan for issues only and don't prompt to fix
      |""".stripMargin
}
