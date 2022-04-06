import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.nio.file.{Files, Paths}

class TaskBuilderTest extends AnyFlatSpec with should.Matchers {

  if (!Files.exists(Paths.get(".canary"))) {
    Files.createDirectory(Paths.get(".canary"))
  }

  new CanaryRepository("https://canary.wiles.fyi", ".canary")
    .downloadPackage("win32-test", "0.1.1")

  "TaskBuilder" should "get a list of tasks for a specific directory" in {
    TaskBuilder.fromDir(Paths.get(".canary", "win32-test", "0.1.1").toString).get.length should be (4)
  }

  it should "return none if the directory doesn't exist" in {
    TaskBuilder.fromDir("asdf").isEmpty should be (true)
  }

  it should "get a list of tasks from a package" in {
    TaskBuilder.fromPackage(".canary", "win32-test").get.length should be (4)
  }

  it should "return none if the package isn't installed" in {
    TaskBuilder.fromPackage(".canary", "asdf").isEmpty should be (true)
    TaskBuilder.fromPackage(".canary", "asdf@1.5.6").isEmpty should be (true)
    TaskBuilder.fromPackage(".canary", "win32-test@1.5.6").isEmpty should be (true)
  }

  it should "return the latest version installed from a list of versions" in {
    TaskBuilder.latestInstalledVersion(Array("0.1.2", "0.1.3")) should be ("0.1.3")
    TaskBuilder.latestInstalledVersion(Array("3.4.4", "1.2.3", "2.2.3")) should be ("3.4.4")
    TaskBuilder.latestInstalledVersion(Array("1.1.2", "1.2.3")) should be ("1.2.3")
  }

}
