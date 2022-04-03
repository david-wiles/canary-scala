import scala.sys.exit

object Canary {
  def fatal(message: String): Nothing = {
    println(message)
    exit(1)
  }

  def main(args: Array[String]): Unit = {
    ArgParse.parse(args.toList)
      .getOrElse(InvalidCommand("Something went wrong. Please contact the maintainers of canary and include steps to replicate the issue"))
      .run()
  }
}
