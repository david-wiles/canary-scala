import java.net.URL

case class InstallCommand(packages: List[String], url: URL, localPath: String) extends Command {
  override def run(): Unit = {
    val repo = new CanaryRepository(url.toString, localPath)

    for (name <- packages) {
      name.split("@") match {
        case pkg :: version :: Nil =>
          repo.downloadPackage(pkg.toString, version.toString)
        case pkg :: Nil =>
          repo.downloadPackage(pkg.toString)
      }
    }
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
      |""".stripMargin
}
