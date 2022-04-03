import scala.io.Source

case class Task(path: String, description: String) {
  def doTask(autoFix: Boolean, skipFix: Boolean): Nothing = ???
}

object Task {
  def create(location: String): Task = {
    val configSoure = Source.fromFile(location + "/config.json")
    val configJSON = try configSoure.mkString finally configSoure.close()

    Task(location, "")
  }
}
