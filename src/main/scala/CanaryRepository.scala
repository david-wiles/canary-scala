import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.utils.IOUtils
import sttp.client3._

import java.io.{BufferedInputStream, File, FileInputStream, FileOutputStream}
import java.nio.file.{Files, Path, Paths}
import java.security.MessageDigest
import scala.io.Source

/**
 * CanaryRepository stores state and methods required to download and update
 * canary packages
 *
 * @param domain    url for the remote canary repository
 * @param localRoot local canary root
 */
class CanaryRepository(domain: String, localRoot: String) {

  private val backend = HttpURLConnectionBackend()

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
        case Some(version) => Files.exists(Paths.get(localRoot, name, version))
      }
    } else {
      Files.exists(Paths.get(localRoot, name, version))
    }
  }

  /**
   * Verify the downloaded checksum against the tarball
   *
   * @param tarball  the tarball file
   * @param checksum the checksum file
   * @return true if checksum matches, false if not
   */
  def verifyPackage(tarball: File, checksum: File): Boolean = {
    if (!tarball.exists() || !checksum.exists()) {
      false
    } else {
      val digest = MessageDigest.getInstance("SHA-256")
        .digest(Files.readAllBytes(tarball.toPath))

      val hashBuilder = new StringBuilder()
      for (i <- 0 until digest.length) {
        hashBuilder.append(Integer.toString((digest(i) & 0xff) + 0x100, 16).substring(1))
      }

      val sumSource = Source.fromFile(checksum)
      val sum = (try sumSource.mkString finally sumSource.close()).split("\\s")

      if (sum.nonEmpty) {
        sum(0).equals(hashBuilder.toString())
      } else {
        false
      }
    }
  }

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
      case Left(err) => None
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
    val pkgFilename: String = if (version.equals("latest")) {
      getLatestVersionString(name) match {
        case None =>
          println(s"Unable to determine latest version for package $name. Skipping package...")
          return
        case Some(version) => s"${name}_$version.tar.gz"
      }
    } else {
      s"${name}_$version.tar.gz"
    }

    basicRequest
      .response(asFile(new File(Paths.get(localRoot, pkgFilename).toString)))
      .get(uri"$domain/$name/$pkgFilename")
      .send(backend)
      .body match {
      case Left(err) => println("Could not download package: " + err)
      case Right(tar) =>
        println("Successfully downloaded package!")

        // Verify checksum
        basicRequest
          .response(asFile(new File(Paths.get(localRoot, s"$pkgFilename.sum").toString)))
          .get(uri"$domain/$name/$pkgFilename.sum")
          .send(backend)
          .body match {
          case Left(err) => println("Could not download checksum to verify package: " + err)
          case Right(sum) =>
            println("Successfully downloaded checksum!")

            if (!verifyPackage(tar, sum)) {
              println(s"Checksum verification unsuccessful. Try downloading the package and verifying manually from $domain")
            } else {
              println("Checksum verification successful.")
              println(untarPackage(localRoot, tar)
                .getOrElse("Successfully extracted package into " + localRoot))
            }

            // Clean up files
            tar.delete()
            sum.delete()
            println("Deleted archive files.")
        }
    }
  }

  /**
   * Extract a downloaded package tarball into the proper folder in the same directory. If the name of
   * the tarball is pkg_2.1.1.tar.gz, the files will be extracted into pkg/2.1.1/...
   *
   * @param root the canary package root
   * @param file the tarball
   * @return if any exceptions are caught, they will be returned as a string
   */
  def untarPackage(root: String, file: File): Option[String] = {
    try {
      val inputStream = new TarArchiveInputStream(
        new GzipCompressorInputStream(
          new BufferedInputStream(
            new FileInputStream(
              file
            )
          )
        )
      )
      var entry: TarArchiveEntry = inputStream.getNextTarEntry
      while (entry != null) {
        val outputPath = Paths.get(root, entry.getName)
        if (entry.isDirectory) {
          Files.createDirectories(outputPath)
        } else {
          Files.createDirectories(outputPath.getParent)
          IOUtils.copy(inputStream, new FileOutputStream(outputPath.toFile))
        }

        entry = inputStream.getNextTarEntry
      }

      inputStream.close()

      None
    } catch {
      case e: Throwable => Some(e.getMessage)
    }
  }
}
