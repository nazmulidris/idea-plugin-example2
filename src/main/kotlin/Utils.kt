import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationManager.getApplication
import java.text.SimpleDateFormat
import java.util.*

/**
 * DSL to print colorized console output. Here are examples of how to use this:
 * ```
 * fun main() {
 *   colorConsole {//this: ColorConsoleContext
 *     printLine {//this: MutableList<String>
 *       span(Purple, "word1")
 *       span("word2")
 *       span(Blue, "word3")
 *     }
 *     printLine {//this: MutableList<String>
 *       span(Green, "word1")
 *       span(Purple, "word2")
 *     }
 *     println(
 *         line {//this: MutableList<String>
 *           add(Green("word1"))
 *           add(Blue("word2"))
 *         })
 *   }
 * }
 * ```
 */
class ColorConsoleContext {
  companion object {
    fun colorConsole(block: ColorConsoleContext.() -> Unit) {
      ColorConsoleContext().apply(block)
    }
  }

  fun printLine(block: MutableList<String>.() -> Unit) {
    if (isPluginInTestIDE() || isPluginInUnitTestMode()) {
      println(line {
        block(this)
      })
    }
  }

  fun line(block: MutableList<String>.() -> Unit): String {
    val messageFragments = mutableListOf<String>()
    block(messageFragments)
    val timestamp = SimpleDateFormat("hh:mm:sa").format(Date())
    return messageFragments.joinToString(separator = ", ", prefix = "$timestamp: ")
  }

  /**
   * Appends all arguments to the given [MutableList].
   */
  fun MutableList<String>.span(color: Colors, text: String): MutableList<String> {
    add(color.ansiCode + text + Colors.ANSI_RESET.ansiCode)
    return this
  }

  /**
   * Appends all arguments to the given [MutableList].
   */
  fun MutableList<String>.span(text: String): MutableList<String> {
    add(text + Colors.ANSI_RESET.ansiCode)
    return this
  }

  fun printWhichThread() {
    colorConsole {
      printLine {
        span(whichThread())
      }
    }
  }

  fun printDebugHeader() {
    val stackTrace = Thread.currentThread().stackTrace[2]
    colorConsole {
      printLine {
        span(Colors.Purple, "${stackTrace.className}.${stackTrace.methodName}()")
      }
    }
  }

  /**
   * [ApplicationManager.getApplication#isDispatchThread] is equivalent to calling [java.awt.EventQueue.isDispatchThread].
   */
  private fun whichThread() = buildString {
    append(
        when {
          getApplication().isDispatchThread -> Colors.Red("Running on EDT")
          else                              -> Colors.Green("Running on BGT")
        }
    )
    append(
        " - ${Thread.currentThread().name.take(50)}..."
    )
  }
}

enum class Colors(val ansiCode: String) {
  ANSI_RESET("\u001B[0m"),
  Black("\u001B[30m"),
  Red("\u001B[31m"),
  Green("\u001B[32m"),
  Yellow("\u001B[33m"),
  Blue("\u001B[34m"),
  Purple("\u001B[35m"),
  Cyan("\u001B[36m"),
  White("\u001B[37m");

  operator fun invoke(content: String): String {
    return "${ansiCode}$content${ANSI_RESET.ansiCode}"
  }

  operator fun invoke(content: StringBuilder): StringBuilder {
    return StringBuilder("${ansiCode}$content${ANSI_RESET.ansiCode}")
  }
}

const val GROUP_DISPAY_ID = "MyPlugin.Group"

/**
 * Generate a [Notification] in IDEA.
 * @param first title of the notification
 * @param second content of the notification
 */
fun notify(first: String, second: String) = com.intellij.notification
    .Notifications.Bus
    .notify(Notification(GROUP_DISPAY_ID,
                         first,
                         second,
                         NotificationType.INFORMATION,
                         NotificationListener.URL_OPENING_LISTENER))

fun isPluginInTestIDE(): Boolean = System.getProperty("idea.is.internal")?.toBoolean() ?: false

fun isPluginInUnitTestMode(): Boolean = ApplicationManager.getApplication().isUnitTestMode