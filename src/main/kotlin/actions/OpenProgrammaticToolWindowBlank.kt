package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import notify
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

internal class OpenProgrammaticToolWindowBlank : AnAction() {
  private val ID = "BlankToolWindow"
  private val myToolWindowActionBuilder = OpenComplexProgrammaticToolWindowActionBuilder(ID) {
    JPanel(BorderLayout()).apply { add(BorderLayout.CENTER, JLabel("Empty JPanel for project ${it.name}")) }
  }
  private val myAction: AnAction = myToolWindowActionBuilder.buildAction()
  override fun actionPerformed(e: AnActionEvent) {
    myAction.actionPerformed(e)
    notify("Showing tool window $ID", "With blank content")
  }
}