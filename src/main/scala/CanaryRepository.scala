import sttp.client3._

import java.nio.file.{Files, Paths}

class CanaryRepository(url: String, localRoot: String) {

  private val backend = HttpURLConnectionBackend()

  /**
   * Checks whether the specified package exists in the repository
   * @param name name of the package. This should correspond to a tarball in the local directory
   * @return     true if package exists, false if not
   */
  def hasPackage(name: String, version: String = ""): Boolean = {
    if (version == "latest") {
      // Check that we have the latest version
      false
    } else {
      Files.exists(Paths.get(localRoot, s"$name.tar.gz"))
    }
  }

  /**
   * Verify the checksum stored in the remote repository to verify the local version has not b
   * been tampered
   * @param name name of the package. This should correspond to a tarball and .version file in the local directory
   * @return     true if checksum matches, false if not
   */
//  def verifyPackage(name: String): Boolean = {
//    val tarballPath = Paths.get(localRoot, s"$name.tar.gz")
//    val versionPath = Paths.get(localRoot, s"$name.version")
//
//    if (!Files.exists(tarballPath) || !Files.exists(versionPath)) {
//      false
//    } else {
//      val digest = MessageDigest.getInstance("SHA-256")
//        .digest()
//    }
//  }

  def updatePackage(name: String): Unit = {

  }

  def downloadPackage(name: String, version: String = "latest"): Option[String] = {
    val resp = basicRequest
      .response(asString)
      .get(uri"$url/$name/index.html")
      .send(backend)

    Some(resp.body.left.get)
  }
}
