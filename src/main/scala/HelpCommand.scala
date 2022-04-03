case class HelpCommand(output: String) extends Command {
  override def run(): Unit = println(output)
}
