import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ArgParseTest extends AnyFlatSpec with should.Matchers {

  "ArgParse" should "return usage messages when invoked without arguments or with --help" in {
    val argparse = new ArgParse()
    argparse.parse(Nil).get should be (HelpCommand(argparse.usage))
    argparse.parse(List("--help")).get should be (HelpCommand(argparse.usage))
    argparse.parse(List("install", "--help")).get should be (HelpCommand(InstallCommand.usage))
    argparse.parse(List("upgrade", "--help")).get should be (HelpCommand(UpgradeCommand.usage))
    argparse.parse(List("check", "--help")).get should be (HelpCommand(CheckCommand.usage))
  }

  it should "return invalid message when invoked with bad commands or invalid arguments" in {
    val argparse = new ArgParse()
    argparse.parse(List("asdf")).get should be (InvalidCommand("Invalid command provided: asdf"))

    argparse.parse(List("install", "--asdf")).get should be (InvalidCommand("Invalid option provided: --asdf"))
    argparse.parse(List("upgrade", "--asdf")).get should be (InvalidCommand("Invalid option provided: --asdf"))
    argparse.parse(List("check", "--asdf")).get should be (InvalidCommand("Invalid option provided: --asdf"))

    argparse.parse(List("install", "--local")).get should be (InvalidCommand("Missing local directory associated with --local"))
    argparse.parse(List("upgrade", "--local")).get should be (InvalidCommand("Missing local directory associated with --local"))
    argparse.parse(List("check", "--local")).get should be (InvalidCommand("Missing local directory associated with --local"))

    argparse.parse(List("install")).get should be (InvalidCommand("Must provide a package to install" + sys.props("line.separator") + InstallCommand.usage))
    argparse.parse(List("upgrade")).get should be (InvalidCommand("Must provide a package to upgrade" + sys.props("line.separator") + UpgradeCommand.usage))
    argparse.parse(List("check")).get should be (InvalidCommand("Must provide one of packages or directory" + sys.props("line.separator") + CheckCommand.usage))
  }

  it should "return an install command when invoked with proper install arguments" in {
    val argparse = new ArgParse()
    argparse.parse(List("install", "--local", ".canary", "--repo", "https://example.com", "asdf", "asdf@1.2", "qwerty")).get should be (InstallCommand(List("qwerty", "asdf@1.2", "asdf"), "https://example.com", ".canary"))
  }

  it should "return an upgrade command when invoked with proper upgrade arguments" in {
    val argparse = new ArgParse()
    argparse.parse(List("upgrade", "--local", ".canary", "--repo", "https://example.com", "asdf", "asdf@1.2", "qwerty")).get should be (UpgradeCommand(List("qwerty", "asdf@1.2", "asdf"), "https://example.com", ".canary"))
  }

  it should "return a check command when invoked with proper check arguments" in {
    val argparse = new ArgParse()
    argparse.parse(List("check", "--local", ".canary", "--auto-fix", "--scan-only", "--directory", "asdf", "asdf@1.2", "qwerty")).get should be (CheckCommand(List("qwerty", "asdf@1.2"), Some("asdf"), ".canary", autoFix = true, scanOnly = true))
  }
}
