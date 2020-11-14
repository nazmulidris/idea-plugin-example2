import color_console_log.ColorConsoleContext.Companion.colorConsole
import color_console_log.Colors.Red
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/** This is a simple test just for demonstration purposes. */
class SimpleTest : BasePlatformTestCase() {

  @Test
  fun testA() {
    colorConsole {
      printDebugHeader()
      printLine {
        span(Red, "testA() running")
        assertThat("AssertJ").isEqualTo("AssertJ")
      }
    }
  }

}
