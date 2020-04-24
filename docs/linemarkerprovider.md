# linemarkerprovider.md

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Line marker provider](#line-marker-provider)
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
[`LineMarkerProvider.java`](https://github.com/JetBrains/intellij-community/blob/master/platform/lang-api/src/com/intellij/codeInsight/daemon/LineMarkerProvider.java)).

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
> ```
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
> ```
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
