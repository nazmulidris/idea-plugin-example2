package actions

import color_console_log.ColorConsoleContext.Companion.colorConsole
import color_console_log.Colors
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.time.LocalDate
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * [JetBrains Docs](https://jetbrains.org/intellij/sdk/docs/user_interface_components/tool_windows.html)
 */
internal class AnotherToolWindow : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val project: Project = e.getRequiredData(CommonDataKeys.PROJECT)
    val toolWindowManager = ToolWindowManager.getInstance(project)
    var toolWindow = toolWindowManager.getToolWindow(ID)

    // One time registration of the tool window (does not add any content).
    if (toolWindow == null) {
      toolWindow = toolWindowManager.registerToolWindow(
          RegisterToolWindowTask(
              id = ID,
              icon = IconLoader.getIcon("/icons/ic_extension.svg", javaClass),
              component = null,
              canCloseContent = true,
          ))
      toolWindow.setToHideOnEmptyContent(true)
    }

    val contentManager = toolWindow.contentManager
    val contentFactory: ContentFactory = ContentFactory.SERVICE.getInstance()
    val contentTab = contentFactory.createContent(
        createComponent(project),
        "${LocalDate.now()}",
        false)
    contentTab.setDisposer {
      colorConsole {
        printLine {
          span(Colors.Purple, "contentTab is disposed, contentCount: ${contentManager.contentCount}")
        }
      }
      if (contentManager.contents.isEmpty()) {
        toolWindowManager.unregisterToolWindow(ID)
      }
    }
    contentManager.addContent(contentTab)
    toolWindow.show {
      // In order to request focus, the contentTab must already have been added to contentManager.
      contentManager.setSelectedContent(contentTab, true)
    }
  }

  private fun createComponent(project: Project): JComponent {
    val panel = JPanel(BorderLayout())
    panel.add(BorderLayout.CENTER, JLabel("TODO add component"))
    return panel
  }

  companion object {
    const val ID = "AnotherToolWindow"
  }
}