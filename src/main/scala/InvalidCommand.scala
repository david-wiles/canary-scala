case class InvalidCommand(message: String) extends Command {
  override def run(): Unit = Canary.fatal(message)
}
