import java.io.File
import java.nio.file.Paths
import scala.collection.mutable.ListBuffer

/**
 * TaskBuilder provides methods to create lists of tasks for Canary from a directory or package
 */
object TaskBuilder {
  /**
   * Creates a ListBuffer containing tasks using the given directory
   *
   * @param dir path to the directory
   * @return the tasks contained in the specified directory
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

  /**
   * Creates a buffer of tasks from a package name. If no version is specified, then the
   * latest is used. The packages are stored in the canary directory in two levels: the
   * first level is the name of the package, and the second level is the version of the
   * package. By listing the packages in the second directory and choosing the version
   * with the highest number we use the latest package
   *
   * @param root    the canary package sources root
   * @param pkgName name of the package to gather tasks from, formatted as pkg@version
   * @return
   */
  def fromPackage(root: String, pkgName: String): Option[ListBuffer[Task]] = {
    pkgName.split('@') match {
      case Array(name, version) => fromDir(Paths.get(root, name, version).toString)
      case Array(name) =>
        fromDir(
          Paths.get(
            root,
            name,
            latestVersion(new File(Paths.get(root, name).toString).list())).toString
        )
    }
  }

  /**
   * Get the latest version number from a list of version numbers
   *
   * @param versions the list of version numbers
   * @return
   */
  def latestVersion(versions: Array[String]): String = {
    var latest = Array(0, 0, 0)
    for (version <- versions) {
      version.split('.') match {
        case Array(major, minor, update) =>
          if (major.toInt > latest(0) ||
            major.toInt == latest(0) && minor.toInt > latest(1) ||
            major.toInt == latest(0) && minor.toInt == latest(1) && update.toInt > latest(2)) {
            latest = Array(major.toInt, minor.toInt, update.toInt)
          }
        case Array(major, minor) =>
          if (major.toInt > latest(0) ||
            major.toInt == latest(0) && minor.toInt > latest(1)) {
            latest = Array(major.toInt, minor.toInt, 0)
          }
        case Array(major) =>
          if (major.toInt > latest(0)) {
            latest = Array(major.toInt, 0, 0)
          }
      }
    }
    latest
      .map(v => v.toString)
      .mkString
  }
}
