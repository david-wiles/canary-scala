import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.nio.file.{Files, Paths}
import scala.reflect.io.Directory

class CanaryRepositoryTest extends AnyFlatSpec with should.Matchers {

  val testRoot = Paths.get(".canaryTest")
  new Directory(testRoot.toFile).deleteRecursively()
  Files.createDirectory(testRoot)

  val testRepo = new CanaryRepository("https://canary.wiles.fyi", ".canary")
  var testLatest = "0.0.0"

  "CanaryRepository" should "return false when a package is not installed" in {
    testRepo.hasPackage("asdf", "latest") should be (false)
  }

  it should "download the latest package and unpack the tar archive" in {
    testRepo.downloadPackage("win32-test", "latest")
    Files.exists(Paths.get(".canary", "win32-test")) should be (true)
  }

  it should "download a specific package and unpack the tar archive" in {
    testRepo.downloadPackage("win32-test", "0.1.0")
    Files.exists(Paths.get(".canary", "win32-test", "0.1.0")) should be (true)
  }

  it should "have a package after downloading" in {
    testRepo.hasPackage("win32-test", "0.1.0") should be (true)
    testRepo.hasPackage("win32-test", "latest") should be (true)
  }

  it should "get the latest version for a package" in {
    testLatest = testRepo.getLatestVersionString("win32-test").getOrElse("0.0.0")
    testLatest should not be ("0.0.0")
  }

  it should "upgrade a package when the latest isn't installed" in {
    new Directory(Paths.get(".canary", "win32-test", testLatest).toFile).deleteRecursively()
    testRepo.updatePackage("win32-test")
    Files.exists(Paths.get(".canary", "win32-test", testLatest)) should be (true)
  }

  it should "return false when a checksum doesn't match" in {
    val tarPath = Files.write(Paths.get(".canary", "ark.tar.gz"), "asdf".getBytes())
    val sumPath = Files.write(Paths.get(".canary", "ark.tar.gz.sum"), "asdf asdf".getBytes())
    testRepo.verifyPackage(tarPath.toFile, sumPath.toFile) should be (false)
    tarPath.toFile.delete()
    sumPath.toFile.delete()
  }

  new Directory(testRoot.toFile).deleteRecursively()
}
