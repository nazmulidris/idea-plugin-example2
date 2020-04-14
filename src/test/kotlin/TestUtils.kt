@file:JvmName("TestUtils")

import TestUtils.Companion.computeBasePath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

/**
 * Files needed to be loaded from the plugin project's `testdata` directory for the IDE test fixtures. This class
 * provides a way to access the files in this directory.
 *
 * By default, IntelliJ Platform [BasePlatformTestCase] provides a location that is invalid for use by 3rd party plugins
 * (provided by [BasePlatformTestCase.getTestDataPath]). This assumes that the files are in the classpath of the
 * IntelliJ IDEA codebase itself.
 *
 * The [computeBasePath] function uses the classpath of this class in order to locate where on disk, this class is
 * loaded from. And then walks up the path to locate the `testdata` folder. Also, note that this class uses an
 * annotation (`@file:JvmName()`) in order to explicitly set its own classname and not use the computed
 * `TestUtilsKt.class` (which would be the default w/out using this annotation).
 */
class TestUtils {

  companion object {
    val testDataFolder = "testdata"

    /**
     * @throws [IllegalStateException] if the [testDataFolder] folder can't be found somewhere on the classpath.
     */
    val computeBasePath by lazy {
      val urlFromClassloader =
          TestUtils::class.java.classLoader.getResource("TestUtils.class")
      checkNotNull(urlFromClassloader) { "Could not find $testDataFolder" }

      var path: File? = File(urlFromClassloader.toURI())
      while (path != null && path!!.exists() && !File(path, testDataFolder).isDirectory) {
        path = path!!.parentFile
      }
      checkNotNull(path) { "Could not find $testDataFolder" }
      File(path, testDataFolder).absolutePath
    }

  }
}

object TestFile {
  val Input = "input-file.md"
  val Output = "output-file.md"
  fun Input(prefix: String): String = "$prefix-$Input"
  fun Output(prefix: String): String = "$prefix-$Output"
}
