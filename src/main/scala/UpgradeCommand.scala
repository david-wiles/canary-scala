import java.net.URL
import java.nio.file.Path

case class UpgradeCommand(packages: List[String], url: URL, localPath: Path) extends Command {
  override def run(): Unit = {
    println(packages)
    println(url)
    println(localPath)
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
      |    --local Path to local canary packages. Default location is ~/.canary
      |""".stripMargin
}
