package ui

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.util.Function
import org.intellij.plugins.markdown.lang.MarkdownElementTypes
import org.intellij.plugins.markdown.lang.MarkdownTokenTypeSets
import psi.findChildElement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.swing.Icon


class MarkdownLineMarkerProvider : LineMarkerProvider {

  private val myTokenSetOfMatchingElementTypes = TokenSet.create(MarkdownElementTypes.INLINE_LINK)

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (!myTokenSetOfMatchingElementTypes.contains(element.node.elementType)) return null
    return RunLineMarkerInfo(element,
                             IconLoader.getIcon("/icons/ic_linemarkerprovider.svg"),
                             createActionGroup(element),
                             createToolTipProvider(element))
  }

  private fun createToolTipProvider(inlineLinkElement: PsiElement): Function<in PsiElement, String> {
    val linkDestinationElement =
        findChildElement(inlineLinkElement, MarkdownTokenTypeSets.LINK_DESTINATION, null)
    val linkDestination = linkDestinationElement?.text
    val tooltipProvider =
        Function { element: PsiElement ->
          val current = LocalDateTime.now()
          val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
          val formatted = current.format(formatter)
          buildString {
            append("Tooltip calculated at ")
            append(formatted)
            append(", for URL: $linkDestination")
          }
        }
    return tooltipProvider
  }

  fun createActionGroup(inlineLinkElement: PsiElement): DefaultActionGroup {
    val linkDestinationElement =
        findChildElement(inlineLinkElement, MarkdownTokenTypeSets.LINK_DESTINATION, null)
    val linkDestination = linkDestinationElement?.text
    val group = DefaultActionGroup()
    group.add(OpenUrlAction(linkDestination))
    group.add(ActionManager.getInstance().getAction("MyPlugin.OpenToolWindowAction"))
    return group
  }

}

class RunLineMarkerInfo(element: PsiElement,
                        icon: Icon,
                        private val myActionGroup: DefaultActionGroup,
                        tooltipProvider: Function<in PsiElement, String>?
) : LineMarkerInfo<PsiElement>(element,
                               element.textRange,
                               icon,
                               tooltipProvider,
                               null,
                               GutterIconRenderer.Alignment.CENTER) {
  override fun createGutterRenderer(): GutterIconRenderer? {
    return object : LineMarkerGutterIconRenderer<PsiElement>(this) {
      override fun getClickAction(): AnAction? {
        return null
      }

      override fun isNavigateAction(): Boolean {
        return true
      }

      override fun getPopupMenuActions(): ActionGroup? {
        return myActionGroup
      }
    }
  }
}

class OpenUrlAction(val linkDestination: String?) :
  AnAction("Open Link", "Open URL destination in browser", IconLoader.getIcon("/icons/ic_extension.svg")) {
  override fun actionPerformed(e: AnActionEvent) {
    linkDestination?.apply {
      BrowserUtil.open(this)
    }
  }

}