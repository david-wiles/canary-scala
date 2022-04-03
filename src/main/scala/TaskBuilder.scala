import java.io.File
import scala.collection.mutable.ListBuffer

/**
 * TaskBuilder provides methods to create lists of tasks for Canary from a directory or package
 */
object TaskBuilder {
  /**
   * Creates a ListBuffer containing tasks using the given directory
   * @param dir path to the directory
   * @return    the tasks contained in the specified directory
   */
  def fromDir(dir: String): Option[ListBuffer[Task]] = {
    val directory = new File(dir)
    var taskList: Option[ListBuffer[Task]] = None

    if (directory.exists && directory.isDirectory) {
      val tasks: ListBuffer[Task] = ListBuffer()
      for (file <- directory.listFiles if file.isDirectory) {
        tasks += Task.create(file.getAbsolutePath)
      }
      taskList = Some(tasks)
    }

    taskList
  }
}
