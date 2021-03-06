import java.nio.file.Paths

/**
 * ArgParse provides a way to specify commands and arguments for Canary without any
 * extra third party packages. Parsing is simple and only full flags are supported.
 * https://stackoverflow.com/questions/2315912/best-way-to-parse-command-line-parameters
 */
class ArgParse {
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

  private val repo: String = "https://canary.wiles.fyi"
  private var local: String = Paths.get(System.getProperty("user.home"), ".canary").toString

  def parse(args: List[String]): Option[Command] = {
    if (args.isEmpty) Option(HelpCommand(usage))
    else {
      args match {
        case Nil => Option(InvalidCommand(usage))
        case "install" :: "--help" :: tail => Option(HelpCommand(InstallCommand.usage))
        case "install" :: tail => parseInstallArgs(tail)
        case "upgrade" :: "--help" :: tail => Option(HelpCommand(UpgradeCommand.usage))
        case "upgrade" :: tail => parseUpgradeArgs(tail)
        case "check" :: "--help" :: tail => Option(HelpCommand(CheckCommand.usage))
        case "check" :: tail => parseCheckArgs(tail)
        case "remove" :: "--help" :: tail => Option(HelpCommand(RemoveCommand.usage))
        case "remove" :: tail => parseRemoveArgs(tail)
        case "--help" :: tail => Option(HelpCommand(usage))
        case string :: tail => Option(InvalidCommand("Invalid command provided: " + string))
      }
    }
  }

  /**
   * Parse args used by all commands
   *
   * @param switch the flag found
   * @param list   the rest of the command line arguments
   * @param next   function to call for the next step in parsing flags
   * @return
   */
  private def parsePersistentArgs(switch: String, list: List[String], next: List[String] => Option[Command]): Option[Command] = {
    switch match {
      case "--local" =>
        if (list.nonEmpty) {
          local = list.head
          next(list.tail)
        } else {
          Option(InvalidCommand("Missing local directory associated with --local"))
        }
      case _ => Option(InvalidCommand("Invalid option provided: " + switch))
    }
  }

  /**
   * Parse flags used by the install command
   *
   * @param args command line arguments
   * @return
   */
  private def parseInstallArgs(args: List[String]): Option[Command] = {
    var packages: List[String] = List()
    var urlString = repo

    def _parse(args: List[String]): Option[Command] = {
      args match {
        case Nil =>
          if (packages.nonEmpty) {
            Option(InstallCommand(packages, urlString, local))
          } else {
            Option(InvalidCommand("Must provide a package to install" + sys.props("line.separator") + InstallCommand.usage))
          }
        case "--repo" :: url :: tail =>
          urlString = url
          _parse(tail)
        case string :: tail =>
          if (string(0) == '-') {
            parsePersistentArgs(string, tail, _parse)
          } else {
            packages = string :: packages
            _parse(tail)
          }
      }
    }

    _parse(args)
  }

  /**
   * Parse flags used by the upgrade command
   *
   * @param args command line arguments
   * @return
   */
  private def parseUpgradeArgs(args: List[String]): Option[Command] = {
    var packages: List[String] = List()
    var urlString = repo

    def _parse(args: List[String]): Option[Command] = {
      args match {
        case Nil =>
          if (packages.nonEmpty) {
            Option(UpgradeCommand(packages, urlString, local))
          } else {
            Option(InvalidCommand("Must provide a package to upgrade" + sys.props("line.separator") + UpgradeCommand.usage))
          }
        case "--repo" :: url :: tail =>
          urlString = url
          _parse(tail)
        case string :: tail =>
          if (string(0) == '-') {
            parsePersistentArgs(string, tail, _parse)
          } else {
            packages = string :: packages
            _parse(tail)
          }
      }
    }

    _parse(args)
  }

  /**
   * Parse flags used by the check command
   *
   * @param args command line arguments
   * @return
   */
  private def parseCheckArgs(args: List[String]): Option[Command] = {
    var packages: List[String] = List()
    var dir: Option[String] = None
    var autoFix = false
    var scanOnly = false

    def _parse(args: List[String]): Option[Command] = {
      args match {
        case Nil =>
          if (packages.isEmpty && dir.isEmpty) {
            Option(InvalidCommand("Must provide one of packages or directory" + sys.props("line.separator") + CheckCommand.usage))
          } else {
            Option(CheckCommand(packages, dir, local, autoFix, scanOnly))
          }
        case "--scan-only" :: tail =>
          scanOnly = true
          _parse(tail)
        case "--auto-fix" :: tail =>
          autoFix = true
          _parse(tail)
        case "--directory" :: path :: tail =>
          dir = Some(path)
          _parse(tail)
        case string :: tail =>
          if (string(0) == '-') {
            parsePersistentArgs(string, tail, _parse)
          } else {
            packages = string :: packages
            _parse(tail)
          }
      }
    }

    _parse(args)
  }

  /**
   * Parse arguments to remove packages
   * @param args command line arguments
   * @return either a RemoveCommand, InvalidCommand, or HelpCommand
   */
  private def parseRemoveArgs(args: List[String]): Option[Command] = {
    var packages: List[String] = List()

    def _parse(args: List[String]): Option[Command] = {
      args match {
        case Nil =>
          if (packages.isEmpty)
            Option(InvalidCommand("Must provide a package to remove" + sys.props("line.separator") + RemoveCommand.usage))
          else
            Option(RemoveCommand(local, packages))
        case string :: tail =>
          if (string(0) == '-') {
            parsePersistentArgs(string, tail, _parse)
          } else {
            packages = string :: packages
            _parse(tail)
          }
      }
    }

    _parse(args)
  }
}
