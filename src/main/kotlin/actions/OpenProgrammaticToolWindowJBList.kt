package actions

import Utils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import javax.swing.*

/**
 * More info on lists:
 * 1. [Swing JList Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/components/list.html)
 * 2. [JetBrains List and Tree Controls docs](https://jetbrains.org/intellij/sdk/docs/user_interface_components/lists_and_trees.html)
 */
internal class OpenProgrammaticToolWindowJBList : AnAction() {
  private val ID = "JBListToolWindow"

  private val myToolWindowActionBuilder = OpenComplexProgrammaticToolWindowActionBuilder(ID) {
    JPanel(BorderLayout())
        .apply {
          val jbList = createJBList()
          val jbScrollPane = JBScrollPane(jbList)
          add(BorderLayout.CENTER, jbScrollPane)
        }
  }

  private val myAction: AnAction = myToolWindowActionBuilder.buildAction()

  override fun actionPerformed(e: AnActionEvent) {
    myAction.actionPerformed(e)
    Utils.notify("Showing tool window $ID", "With JBList content")
  }

  private fun createJBList(): JBList<*> {
    val list = JBList<String>()
    val modelHolder: ModelHolder<String> = createListModel()

    list.apply {
      model = modelHolder.model
      selectionMode = ListSelectionModel.SINGLE_INTERVAL_SELECTION
      layoutOrientation = JList.VERTICAL
    }

    with(list) {
      attachSelectionListener(this)
      selectedIndex = 0 // Trigger a selection event.
    }

    return list
  }

  private fun attachSelectionListener(list: JBList<String>) = list.addListSelectionListener {
    if (it.valueIsAdjusting || list.selectedIndex == -1) return@addListSelectionListener
    Utils.notify("List item selected", "item : ${list.selectedIndex}, value : ${list.selectedValue}")
  }

  data class ModelHolder<T>(val model: ListModel<T>, val underlyingList: MutableList<T>)

  private fun createListModel(): ModelHolder<String> {
    val model = DefaultListModel<String>()
    val underlyingList = mutableListOf<String>()
    repeat(50) { underlyingList.add(getRandomString(50)) }
    model.addAll(underlyingList)
    return ModelHolder<String>(model, underlyingList)
  }

  private fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
  }
}