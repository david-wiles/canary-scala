import java.io.File
import java.nio.file.Paths

case class RemoveCommand(packageRoot: String, packages: List[String]) extends Command {
  override def run(): Unit = {
    // Delete the file or directory recursively
    def deleteRecursively(result: Boolean, file: File): Boolean = {
      if (!file.isDirectory)
        !(file.exists && !file.delete) && result
      else
        file.listFiles.foldLeft(result)(deleteRecursively) && file.delete()
    }

    for (pkg <- packages) {
      val pkgPath = pkg.split("@") match {
        case Array(name, version) => Paths.get(packageRoot, name, version)
        case Array(name) => Paths.get(packageRoot, name)
      }

      if (deleteRecursively(result = true, new File(pkgPath.toString)))
        println(s"Removed package $pkg")
      else
        println(s"Unable to remove package $pkg")
    }
  }
}

object RemoveCommand {
  val usage: String =
    """
      |Usage: canary remove [OPTIONS] [PACKAGES...]
      |
      |Removes packages from the local system
      |
      |""".stripMargin
}
