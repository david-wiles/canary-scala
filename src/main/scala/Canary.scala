import scala.sys.exit

object Canary {
  /**
   * Used whenever a fatal error occurs and the program should exit immediately.
   * @param message message to print prior to exit
   * @return
   */
  def fatal(message: String): Nothing = {
    println(message)
    exit(1)
  }

  /**
   * Entrypoint for canary
   * @param args command line arguments
   */
  def main(args: Array[String]): Unit = {
    (new ArgParse).parse(args.toList)
      .getOrElse(InvalidCommand("Something went wrong. Please contact the maintainers of canary and include steps to replicate the issue"))
      .run()
  }
}
