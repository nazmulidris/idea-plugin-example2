package ui

import color_console_log.ColorConsoleContext.Companion.colorConsole
import color_console_log.Colors.Purple
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel

fun createDialogPanel(): DialogPanel = panel {
  noteRow("""Note with a link. <a href="http://github.com">Open source</a>""") {
    colorConsole {
      printLine {
        span(Purple, "link url: '$it' clicked")
      }
    }
    BrowserUtil.browse(it)
  }
}