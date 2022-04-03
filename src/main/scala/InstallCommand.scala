import java.net.URL
import java.nio.file.Path

case class InstallCommand(packages: List[String], url: URL, localPath: Path) extends Command {
  override def run(): Unit = {
    println(packages)
    println(url)
    println(localPath)
  }
}

object InstallCommand {
  val usage: String =
    """
      |Usage: canary install [OPTIONS] [PACKAGES...]
      |
      |Installs packages to the local system from a remote repository
      |
      |Options:
      |    --repo URL for the repository to pull from
      |    --local Path to local canary packages. Default location is ~/.canary
      |""".stripMargin
}
