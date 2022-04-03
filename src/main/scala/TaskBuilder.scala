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
    var taskList: Option[ListBuffer[Task]] = Option(null)

    if (directory.exists && directory.isDirectory) {
      val tasks: ListBuffer[Task] = ListBuffer()
      for (file <- directory.listFiles if file.isDirectory) {
        tasks += Task.create(file.getAbsolutePath)
      }
      taskList = Option(tasks)
    }

    taskList
  }

  /**
   * downloadPackages takes a package name and repository information and
   * ensures that the package exists in the location specified by repo.
   * This may download files from a remote server and save them on the user's system.
   * If the package already exists, then nothing will change
   *
   * @param repo        the repository to use and local file storage
   * @param packageName the package to install
   */
  def downloadPackage(repo: CanaryRepository, packageName: String): Unit = {}

  def verify(location: String, key: String) = {}
}
