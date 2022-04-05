import sttp.client3._

import java.io.File
import java.nio.file.{Files, Paths}
import java.security.MessageDigest
import scala.io.Source

/**
 * CanaryRepository stores state and methods required to download and update
 * canary packages
 *
 * @param domain       url for the remote canary repository
 * @param localRoot local canary root
 */
class CanaryRepository(domain: String, localRoot: String) {

  private val backend = CurlBackend()

  /**
   * Checks whether the specified package exists in the repository
   *
   * @param name name of the package. This should correspond to a tarball in the local directory
   * @return true if package exists, false if not
   */
  def hasPackage(name: String, version: String): Boolean = {
    if (version == "latest") {
      getLatestVersionString(name) match {
        case None => false
        case Some(version) => Files.exists(Paths.get(localRoot, s"$name-$version.tar.gz"))
      }
    } else {
      Files.exists(Paths.get(localRoot, s"$name-$version.tar.gz"))
    }
  }

  /**
   * Verify the checksum stored in the remote repository to verify the local version has not b
   * been tampered
   *
   * @param name name of the package. This should correspond to a tarball and .version file in the local directory
   * @return true if checksum matches, false if not
   */
  //  def verifyPackage(name: String, version: String): Boolean = {
  //    val tarballPath = Paths.get(localRoot, s"$name.tar.gz")
  //
  //    if (!Files.exists(tarballPath)) {
  //      false
  //    } else {
  //      val resp = basicRequest
  //        .response(asString)
  //        .get(uri"$url/$name/$name-$version.tar.gz.sum")
  //        .send(backend)
  //
  //      val digest = MessageDigest.getInstance("SHA-256")
  //        .digest(Files.readAllBytes(tarballPath))
  //    }
  //  }

  /**
   * Get latest version of the specified package
   *
   * @param name The package to update
   */
  def updatePackage(name: String): Unit = {
    getLatestVersionString(name) match {
      case None => println(s"Could not update package $name: could not determine latest version")
      case Some(version) =>
        if (hasPackage(name, version)) {
          println(s"Updating $name...")
          downloadPackage(name, version)
        } else {
          println(s"A package named $name could not be found. Please use 'canary install $name' instead.")
        }
    }
  }

  /**
   * Get a string representing the latest version for the specified package. The latest version
   * is stored in a file called version.txt in the package's directory on the remote server.
   *
   * @param name The name of the package
   * @return String representing the version, e.g. 1.0.2
   */
  def getLatestVersionString(name: String): Option[String] = {
    val resp = basicRequest
      .response(asString)
      .get(uri"$domain/$name/version.txt")
      .send(backend)

    resp.body match {
      case Left(err) =>
        println(s"Could not get latest version for package $name: $err")
        None
      case Right(version) => Some(version)
    }
  }

  /**
   * Download the specified version of a package. If no version is specified, the latest will
   * automatically be downloaded along with the checksum for the package
   *
   * @param name    name of the package to download
   * @param version version to get. Default "latest"
   * @return error message if the package could not be downloaded or verified
   */
  def downloadPackage(name: String, version: String): Unit = {
    var versionCode = version
    if (version.equals("latest")) {
      getLatestVersionString(name) match {
        case None =>
          println(s"Unable to determine latest version for package $name. Skipping package...")
          return
        case Some(version) => versionCode = version
      }
    }

    versionCode = versionCode.replace('.', '_')
    val pkgFilename = s"$name-$version.tar.gz"

    basicRequest
      .response(asFile(new File(Paths.get(localRoot, pkgFilename).toString)))
      .get(uri"$domain/$name/$pkgFilename")
      .send(backend)
      .body match {
      case Left(err) => println("Could not download package: " + err)
      case Right(f) => println("Successfully downloaded package!")
    }

    // Verify checksum against remote
    basicRequest
      .response(asFile(new File(Paths.get(localRoot, pkgFilename + ".sum").toString)))
      .get(uri"$domain/$name/$pkgFilename.sum")
      .send(backend)
      .body match {
      case Left(err) => println("Could not download checksum to verify package: " + err)
      case Right(f) => println("Successfull downloaded checksum!")
    }

    // Verify checksum

  }
}
