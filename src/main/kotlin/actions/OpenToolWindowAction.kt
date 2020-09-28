package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindowManager
import ui.createDialogPanel

internal class OpenToolWindowAction : AnAction() {
  val id = "Programmatic tool window"
  override fun actionPerformed(e: AnActionEvent) {
    val toolWindowManager = ToolWindowManager.getInstance(e.getRequiredData(CommonDataKeys.PROJECT))
    var toolWindow = toolWindowManager.getToolWindow(id)

    if (toolWindow == null) {
      val task = RegisterToolWindowTask(
          id = id,
          icon = IconLoader.getIcon("/icons/ic_toolwindow.svg"),
          component = createDialogPanel(),
          canWorkInDumbMode = true
      )
      toolWindow = toolWindowManager.registerToolWindow(task)
    }

    toolWindow.show()
  }
}