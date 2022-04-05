import java.nio.file.{Files, Paths}

/**
 * Upgrade canary packages already installed. If the package is not found it will not be
 * automatically installed to prevent unintentional actions on behalf of the user
 *
 * @param packages  list of packages to upgrade
 * @param domain       canary remote repository
 * @param localPath local canary root
 */
case class UpgradeCommand(packages: List[String], domain: String, localPath: String) extends Command {
  override def run(): Unit = {
    val repo = new CanaryRepository(domain, localPath)
    for (name <- packages) {
      name.split('@') match {
        case Array(pkg, version) =>
          if (Files.exists(Paths.get(localPath, pkg)))
            repo.downloadPackage(pkg, version)
          else
            println(s"Package has not been installed. You can install it with 'canary install $name'")
        case Array(pkg) =>
          if (Files.exists(Paths.get(localPath, pkg)))
            repo.downloadPackage(pkg, "latest")
          else
            println(s"Package has not been installed. You can install it with 'canary install $name'")
      }
    }
  }
}

object UpgradeCommand {
  val usage: String =
    """
      |Usage: canary upgrade [OPTIONS] [PACKAGES...]
      |
      |Upgrades packages already installed
      |
      |Options:
      |    --repo URL for the repository to pull from
      |""".stripMargin
}
