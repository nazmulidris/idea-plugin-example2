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
import kotlin.random.Random

/**
 * [JetBrains Docs](https://jetbrains.org/intellij/sdk/docs/user_interface_components/tool_windows.html)
 */
internal class OpenComplexProgrammaticToolWindowActionBuilder(
    val ID: String = "ComplexToolWindow${Random.nextInt(100)}",
    val createComponentLambda: ((Project) -> JComponent)?
) {
  fun buildAction(): AnAction {
    class MyAction : AnAction() {
      override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.getRequiredData(CommonDataKeys.PROJECT)
        val toolWindowManager = ToolWindowManager.getInstance(project)

        // One time registration of the tool window (does not add any content).
        val toolWindow = toolWindowManager.getToolWindow(ID) ?: toolWindowManager.registerToolWindow(
            RegisterToolWindowTask(
                id = ID,
                icon = IconLoader.getIcon("/icons/ic_extension.svg", javaClass),
                component = null,
                canCloseContent = true,
            )).apply {
          setToHideOnEmptyContent(true)
        }

        val contentManager = toolWindow.contentManager
        val contentFactory: ContentFactory = ContentFactory.SERVICE.getInstance()
        val contentTab = contentFactory.createContent(
            // Use the provided lambda if it exists, otherwise, use the dummy createComponent() method.
            createComponentLambda?.invoke(project) ?: createEmptyComponent(project),
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
    }
    return MyAction()
  }

  private fun createEmptyComponent(project: Project): JComponent {
    val panel = JPanel(BorderLayout())
    panel.add(BorderLayout.CENTER, JLabel("TODO add component"))
    return panel
  }
}