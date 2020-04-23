package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindowManager
import ui.createDialogPanel

internal class OpenToolWindowAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val id = "Programmatic tool window"
    val task = RegisterToolWindowTask(
        id = id,
        icon = IconLoader.getIcon("/icons/ic_toolwindow.svg"),
        component = createDialogPanel()
    )
    val toolWindowManager = ToolWindowManager.getInstance(e.getRequiredData(CommonDataKeys.PROJECT))
    var toolWindow = toolWindowManager.getToolWindow(id)
    if (toolWindow == null) {
      toolWindow = toolWindowManager.registerToolWindow(task)
    }
    toolWindow.show()
  }
}