/**
 * A Command is any action that can be run by canary. It has an associated action which
 * should be initiated by the run() method.
 */
trait Command {
  def run(): Unit
}

case class HelpCommand(output: String) extends Command {
  override def run(): Unit = println(output)
}

case class InvalidCommand(message: String) extends Command {
  override def run(): Unit = Canary.fatal(message)
}
