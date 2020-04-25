# linemarkerprovider.md

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Line marker provider](#line-marker-provider)
  - [Example of a provider for Markdown language](#example-of-a-provider-for-markdown-language)
    - [1. Declare dependencies](#1-declare-dependencies)
    - [2. Register the provider in XML](#2-register-the-provider-in-xml)
    - [3. Provide an implementation of LineMarkerProvider](#3-provide-an-implementation-of-linemarkerprovider)
  - [References](#references)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Line marker provider

Line marker providers allow your plugin to display an icon in the gutter of an editor window. You can also provide
actions that can be run when the user interacts with the gutter icon, along with a tooltip that can be generated when
the user hovers over the gutter icon.

In order to use line marker providers, you have to do two things:

1. Create a class that implements `LineMarkerProvider` that generates the `LineMarkerInfo` for the correct `PsiElement`
   that you want IDEA to highlight in the IDE.
2. Register this provider in `plugin.xml` and associate it to be run for a specific language.

When your plugin is loaded, IDEA will then run your line marker provider when a file of that language type is loaded in
the editor. This happens in two passes for performance reasons.

1. IDEA will first call your provider implementation with the `PsiElements` that are currently visible.
2. IDEA will then call your provider implementation with the `PsiElements` that are currently hidden.

It is very important that you only return a `LineMarkerInfo` for the more specific `PsiElement` that you wish IDEA to
highlight, as if you scope it too broadly, there will be scenarios where your gutter icon will blink! Here's a detailed
explanation as to why (a comment from the source for
[`LineMarkerProvider.java: source file`](https://github.com/JetBrains/intellij-community/blob/master/platform/lang-api/src/com/intellij/codeInsight/daemon/LineMarkerProvider.java)).

> Please create line marker info for leaf elements only - i.e. the smallest possible elements. For example, instead of
> returning method marker for `PsiMethod`, create the marker for the `PsiIdentifier` which is a name of this method.
>
> Highlighting (specifically, `LineMarkersPass`) queries all `LineMarkerProvider`s in two passes (for performance
> reasons):
>
> 1. first pass for all elements in visible area
> 2. second pass for all the rest elements If provider returned nothing for both areas, its line markers are cleared.
>
> So imagine a `LineMarkerProvider` which (incorrectly) written like this:
>
> ```java
> class MyBadLineMarkerProvider implements LineMarkerProvider {
>   public LineMarkerInfo getLineMarkerInfo(PsiElement element) {
>     if (element instanceof PsiMethod) { // ACTUALLY DONT!
>        return new LineMarkerInfo(element, element.getTextRange(), icon, null,null, alignment);
>     }
>     else {
>       return null;
>     }
>   }
>   ...
> }
> ```
>
> Note that it create `LineMarkerInfo` for the whole method body. Following will happen when this method is half-visible
> (e.g. its name is visible but a part of its body isn't):
>
> 1. the first pass would remove line marker info because the whole `PsiMethod` isn't visible
> 2. the second pass would try to add line marker info back because `LineMarkerProvider` was called for the `PsiMethod`
>    at last
>
> As a result, line marker icon will blink annoyingly. Instead, write this:
>
> ```java
> class MyGoodLineMarkerProvider implements LineMarkerProvider {
>   public LineMarkerInfo getLineMarkerInfo(PsiElement element) {
>     if (element instanceof PsiIdentifier &&
>         (parent = element.getParent()) instanceof PsiMethod &&
>         ((PsiMethod)parent).getMethodIdentifier() == element)) { // aha, we are at method name
>          return new LineMarkerInfo(element, element.getTextRange(), icon, null,null, alignment);
>     }
>     else {
>       return null;
>     }
>   }
>   ...
> }
> ```

### Example of a provider for Markdown language

Let's say that for Markdown files that are open in the IDE, we want to highlight any lines that have links in them. We
want an icon to show up in the gutter area that the user can see and click on to take some actions. For example, they
can open the link.

#### 1. Declare dependencies

Also, because we are relying on the Markdown plugin, in our plugin, we have to add the following dependencies.

To `plugin.xml`, we must add.

```xml
<!-- please see http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/build_number_ranges.html for description -->
<idea-version since-build="2020.1" until-build="2020.*" />
<!--
  Declare dependency on IntelliJ module `com.intellij.modules.platform` which provides the following:
  Messaging, UI Themes, UI Components, Files, Documents, Actions, Components, Services, Extensions, Editors
  More info: https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/plugin_compatibility.html
-->
<depends>com.intellij.modules.platform</depends>
<!-- Markdown plugin. -->
<depends>org.intellij.plugins.markdown</depends>
```

To `build.gradle.kts` we must add.

```kotlin
// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    // Information on IJ versions https://www.jetbrains.org/intellij/sdk/docs/reference_guide/intellij_artifacts.html
    // You can use release build numbers or snapshot name for the version.
    // 1) IJ Release Repository w/ build numbers https://www.jetbrains.com/intellij-repository/releases/
    // 2) IJ Snapshots Repository w/ snapshot names https://www.jetbrains.com/intellij-repository/snapshots/
    version = "2020.1" // You can also use LATEST-EAP-SNAPSHOT here.

    // Declare a dependency on the markdown plugin to be able to access the
    // MarkdownRecursiveElementVisitor.kt file. More info:
    // https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_dependencies.html
    // https://plugins.jetbrains.com/plugin/7793-markdown/versions
    setPlugins("java", "org.intellij.plugins.markdown:201.6668.27")
}
```

#### 2. Register the provider in XML

The first thing we need to do is register our line marker provider in `plugin.xml`.

```xml
<extensions defaultExtensionNs="com.intellij">
  <codeInsight.lineMarkerProvider language="Markdown" implementationClass="ui.MarkdownLineMarkerProvider" />
</extensions>
```

#### 3. Provide an implementation of LineMarkerProvider

Then we have to provide an implementation of `LineMarkerProvider` that returns a `LineMarkerInfo` for the most fine
grained `PsiElement` that it successfully matches against. In other words, we can either match against the
`LINK_DESTINATION` or the `LINK_TEXT` elements.

Here's an example of what a Markdown link looks like (from a PSI perspective) for the string
`[`LineMarkerProvider.java`](https://github.com/JetBrains/intellij-community/blob/master/platform/lang-api/src/com/intellij/codeInsight/daemon/LineMarkerProvider.java)`.

```text
ASTWrapperPsiElement(Markdown:Markdown:INLINE_LINK)(1644,1810)
        ASTWrapperPsiElement(Markdown:Markdown:LINK_TEXT)(1644,1671)
          PsiElement(Markdown:Markdown:[)('[')(1644,1645)
          ASTWrapperPsiElement(Markdown:Markdown:CODE_SPAN)(1645,1670)
            PsiElement(Markdown:Markdown:BACKTICK)('`')(1645,1646)
            PsiElement(Markdown:Markdown:TEXT)('LineMarkerProvider.java')(1646,1669)
            PsiElement(Markdown:Markdown:BACKTICK)('`')(1669,1670)
          PsiElement(Markdown:Markdown:])(']')(1670,1671)
        PsiElement(Markdown:Markdown:()('(')(1671,1672)
        MarkdownLinkDestinationImpl(Markdown:Markdown:LINK_DESTINATION)(1672,1809)
          PsiElement(Markdown:Markdown:GFM_AUTOLINK)('https://github.com/JetBrains/intellij-community/blob/master/platform/lang-api/src/com/intellij/codeInsight/daemon/LineMarkerProvider.java')(1672,1809)
        PsiElement(Markdown:Markdown:))(')')(1809,1810)
```

Here's what the implementation of the line marker provider that matches `INLINE_LINK` might look like.

```kotlin
package ui

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.intellij.plugins.markdown.lang.MarkdownElementTypes

internal class MarkdownLineMarkerProvider : LineMarkerProvider {
  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {

    val node = element.node
    val tokenSet = TokenSet.create(MarkdownElementTypes.INLINE_LINK)
    val icon = IconLoader.getIcon("/icons/ic_linemarkerprovider.svg")

    if (tokenSet.contains(node.elementType))
      return LineMarkerInfo(element,
                            element.textRange,
                            icon,
                            null,
                            null,
                            GutterIconRenderer.Alignment.CENTER)

    return null
  }
}
```

You can add the `ic_linemarkerprovider.svg` icon here (create this file in the `$PROJECT_DIR/src/main/resources/icons/` folder.

```xml
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" height="13px" width="13px">
  <path d="M0 0h24v24H0z" fill="none" />
  <path
      d="M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z" />
</svg>
```

### References

Docs

- [JB docs](https://tinyurl.com/yc8mmrdl)

Code samples

- [HaxeLineMarkerProvider.java](https://tinyurl.com/yayn2or6)
- [SimpleLineMarkerProvider.java](https://tinyurl.com/ydgwt96k)
- [Lots of examples of using `LineMarkerInfo`](https://tinyurl.com/y9m2ckm6)

Discussions

- [JB forums: LineMarker for PsiElement, no target](https://tinyurl.com/y7ohdv2v)
- [JB forums: Adding "Actions" to Line Markers](https://tinyurl.com/y8gqlz3y)
- [JB forums: Is it possible to add line markers (gutter icons) without a file change?](https://tinyurl.com/ydhmzew9)
- [JB forums: Set a LineMarker on click, similar to bookmark](https://tinyurl.com/yd49fc44)
