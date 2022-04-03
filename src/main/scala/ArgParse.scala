import java.net.URL
import java.nio.file.{Path, Paths}
import scala.annotation.tailrec

object ArgParse {
  val usage: String =
    """
      |Usage: canary [OPTIONS] [COMMAND]
      |
      |A general-purpose security scanner and resolver
      |
      |Options:
      |    --local Path to local canary packages. Default location is ~/.canary
      |
      |Commands:
      |    install  Installs new packages from the list provided
      |    upgrade  Upgrades packages from the list provided
      |    check    Checks security issues using the given packages
      |
      |Run 'canary COMMAND --help' to view more information about a command
      |""".stripMargin

  def parse(args: List[String]): Option[Command] = {
    if (args.isEmpty) Option(InvalidCommand(usage))
    else {
      args match {
        case Nil => Option(InvalidCommand(usage))
        case "install" :: "--help" :: tail => Option(HelpCommand(InstallCommand.usage))
        case "install" :: tail => parseInstallArgs(tail)
        case "upgrade" :: "--help" :: tail => Option(HelpCommand(UpgradeCommand.usage))
        case "upgrade" :: tail => parseUpgradeArgs(tail)
        case "check" :: "--help" :: tail => Option(HelpCommand(CheckCommand.usage))
        case "check" :: tail => parseCheckArgs(tail)
        case string :: tail => Option(InvalidCommand("Invalid command provided: " + string))
      }
    }
  }

  def parseInstallArgs(args: List[String]): Option[Command] = {
    var packages: List[String] = List()
    var urlString = System.getenv("CANARY_REPO")
    var localPath = "~/.canary"

    @tailrec
    def _parse(args: List[String]): Option[Command] = {
      args match {
        case Nil =>
          if (packages.nonEmpty) {
            Option(InstallCommand(packages, new URL(urlString), Paths.get(localPath)))
          } else {
            Option(InvalidCommand("Must provide a package to install\n" + InstallCommand.usage))
          }
        case "--repo" :: url :: tail =>
          urlString = url
          _parse(tail)
        case "--local" :: path :: tail =>
          localPath = path
          _parse(tail)
        case name :: tail =>
          packages = name :: packages
          _parse(tail)
      }
    }

    _parse(args)
  }

  def parseUpgradeArgs(args: List[String]): Option[Command] = {
    var packages: List[String] = List()
    var urlString = System.getenv("CANARY_REPO")
    var localPath = "~/.canary"

    @tailrec
    def _parse(args: List[String]): Option[Command] = {
      args match {
        case Nil =>
          if (packages.nonEmpty) {
            Option(UpgradeCommand(packages, new URL(urlString), Paths.get(localPath)))
          } else {
            Option(InvalidCommand("Must provide a package to upgrade\n" + UpgradeCommand.usage))
          }
        case "--repo" :: url :: tail =>
          urlString = url
          _parse(tail)
        case "--local" :: path :: tail =>
          localPath = path
          _parse(tail)
        case name :: tail =>
          packages = name :: packages
          _parse(tail)
      }
    }

    _parse(args)
  }

  def parseCheckArgs(args: List[String]): Option[Command] = {
    var packages: List[String] = List()
    var dir: Option[String] = Option(null)
    var localPath = "~/.canary"
    var autoFix = false
    var scanOnly = false

    @tailrec
    def _parse(args: List[String]): Option[Command] = {
      args match {
        case Nil =>
          if (packages.isEmpty && dir.isEmpty) {
            Option(InvalidCommand("Must provide one of packages or directory\n" + CheckCommand.usage))
          } else {
            Option(CheckCommand(packages, dir, localPath, autoFix, scanOnly))
          }
        case "--scan-only" :: tail =>
          scanOnly = true
          _parse(tail)
        case "--auto-fix" :: tail =>
          autoFix = true
          _parse(tail)
        case "--local" :: path :: tail =>
          localPath = path
          _parse(tail)
        case "--directory" :: path :: tail =>
          dir = Option(path)
          _parse(tail)
        case name :: tail =>
          packages = name :: packages
          _parse(tail)
      }
    }

    _parse(args)
  }
}