import java.nio.file.{Files, Paths}

/**
 * InstallCommand is used to install packages to the local canary root
 * @param packages  list of packages to install
 * @param domain       url of remote canary repository
 * @param localPath path to local canary root
 */
case class InstallCommand(packages: List[String], domain: String, localPath: String) extends Command {
  override def run(): Unit = {
    // Ensure canary root exists
    if (!Files.exists(Paths.get(localPath)))
      Files.createDirectories(Paths.get(localPath))

    // Download packages
    val repo = new CanaryRepository(domain, localPath)
    for (name <- packages) {
      name.split('@') match {
        case Array(pkg, version) => repo.downloadPackage(pkg, version)
        case Array(pkg) => repo.downloadPackage(pkg, "latest")
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
