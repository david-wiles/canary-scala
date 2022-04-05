import java.net.URL

/**
 * InstallCommand is used to install packages to the local canary root
 * @param packages  list of packages to install
 * @param url       url of remote canary repository
 * @param localPath path to local canary root
 */
case class InstallCommand(packages: List[String], url: URL, localPath: String) extends Command {
  override def run(): Unit = {
    val repo = new CanaryRepository(url.toString, localPath)
    for (name <- packages) {
      name.split('@') match {
        case Array(pkg, version) => repo.downloadPackage(pkg, version)
        case Array(pkg) => repo.downloadPackage(pkg)
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
