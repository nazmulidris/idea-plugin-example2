package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.components.JBList
import notify
import java.awt.BorderLayout
import javax.swing.JPanel

internal class OpenProgrammaticToolWindowJBList : AnAction() {
  private val ID = "JBListToolWindow"
  private val myToolWindowActionBuilder = OpenComplexProgrammaticToolWindowActionBuilder(ID) {
    JPanel(BorderLayout()).apply { add(BorderLayout.CENTER, createJBList()) }
  }
  private val myAction: AnAction = myToolWindowActionBuilder.buildAction()
  override fun actionPerformed(e: AnActionEvent) {
    myAction.actionPerformed(e)
    notify("Showing tool window $ID", "With JBList content")
  }

  // TODO create a JBList component
  private fun createJBList(): JBList<*> {
    val list = JBList<String>()
    return list
  }
}