# idea-plugin-example2

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Tool windows](#tool-windows)
  - [Declarative tool window](#declarative-tool-window)
  - [Programmatic tool window](#programmatic-tool-window)
  - [Indices and dumb aware](#indices-and-dumb-aware)
- [Creating a content for any kind of tool window](#creating-a-content-for-any-kind-of-tool-window)
  - [Content closeability](#content-closeability)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

This project is aimed at exploring more complex UI elements from IntelliJ Platform SDK. Here is a list:

1. Tool windows
2. Gutters
3. Code editor component

## Tool windows

For both of these types of tool windows, the following applies:

1. Each tool window can have multiple tabs (aka "contents").
2. Each side of the IDE can only show 2 tool windows at any given time, as the primary or the secondary. For eg: you can
   move the "Project" tool window to "Left Top", and move the "Structure" tool window to "Left Bottom". This way you can
   open both of them at the same time. Note that when you move these tool windows to "Left Top" or "Left Bottom" how
   they actually move to the top or bottom of the side of the IDE.

There are two main types of tool windows: Declarative, and Programmatic.

### Declarative tool window

Always visible and the user can interact with it at anytime (eg: Gradle plugin tool window).

- This type of tool window must be registered in `plugin.xml` using the `com.intellij.toolWindow` extension point. You
  can specify things to register this in XML:
  - `id`: Text displayed in the tool window button.
  - `anchor`: Side of the screen in which the tool window is displayed ("left", "right", or "bottom").
  - `secondary`: Specify whether it is displayed in the primary or secondary group.
  - `icon`: Icon displayed in the tool window button (13px x 13px).
  - `factoryClass`: A class implementing `ToolWindowFactory` interface, which is used to instantiate the tool window
    when the user clicks on the tool window button (by calling `createToolWindowContent()`). Note that if a user does
    not interact with the button, then a tool window doesn't get created.
  - For versions 2020.1 and later, also implement the `isApplicable(Project)` method if there's no need to display a
    tool window for all projects. Note this condition is only evaluated the first time a project is loaded.

Here's an example.

The factory class.

```kotlin
class DeclarativeToolWindowFactory : ToolWindowFactory, DumbAware {
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val contentManager = toolWindow.contentManager
    val content = contentManager.factory.createContent(createDialogPanel(), null, false)
    contentManager.addContent(content)
  }
}

fun createDialogPanel(): DialogPanel = panel {
  noteRow("""Note with a link. <a href="http://github.com">Open source</a>""") {
    colorConsole {
      printLine {
        span(Colors.Purple, "link url: '$it' clicked")
      }
    }
    BrowserUtil.browse(it)
  }
}
```

The `plugin.xml` snippet.

```xml
<extensions defaultExtensionNs="com.intellij">
  <toolWindow
      icon="/icons/ic_toolwindow.svg"
      id="Declarative tool window"
      anchor="left"
      secondary="true"
      factoryClass="ui.DeclarativeToolWindowFactory" />
</extensions>
```

### Programmatic tool window

Only visible when a plugin creates it to show the results of an operation (eg: Analyze Dependencies action). This type
of tool window must be added programmatically by calling
`ToolWindowManager.getInstance().registerToolWindow(RegisterToolWindowTask)`. The task that is passed as an argument can
take the values that are passed in `plugin.xml`.

Here's an example.

```kotlin
class DeclarativeToolWindowFactory : ToolWindowFactory, DumbAware {
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val contentManager = toolWindow.contentManager
    val content = contentManager.factory.createContent(createDialogPanel(), null, false)
    contentManager.addContent(content)
  }
}

fun createDialogPanel(): DialogPanel = panel {
  noteRow("""Note with a link. <a href="http://github.com">Open source</a>""") {
    colorConsole {
      printLine {
        span(Colors.Purple, "link url: '$it' clicked")
      }
    }
    BrowserUtil.browse(it)
  }
}
```

The `plugin.xml` snippet, to register the action.

```xml
<actions>
  <action id="MyPlugin.OpenToolWindowAction" class="actions.OpenToolWindowAction" text="Open Tool Window"
      description="Opens tool window programmatically" icon="/icons/ic_extension.svg">
    <add-to-group group-id="EditorPopupMenu" anchor="first" />
  </action>
</actions>
```

### Indices and dumb aware

Displaying the contents of many tool windows requires access to the indices. Because of that, tool windows are normally
disabled while building indices, unless true is passed as the value of `canWorkInDumbMode` to the `registerToolWindow()`
function (for programmatic tool windows). You can also implement `DumbAware` in your factory class to let IDEA know that
your tool window can be shown while indices are being built.

## Creating a content for any kind of tool window

Regardless of the type of tool window (declarative or programmatic) here is the sequence of operations that you have to
perform in order to add a content:

1. Call `ToolWindow.getContentManager()` to get all the contents of a tool window. Eg:
   `val contentManager: ContentManager = toolWindow.contentManager`.
2. Create the component / UI that you need for the content, ie, a Swing component.
3. Add the component / UI to the content using something like:
   `val content = contentManager.factory.createContent(component, ..)`.
4. Then add the content to the tool window by using `contentManager.addContent(content)`.

### Content closeability

A plugin can control whether the user is allowed to close tabs either 1) globally or 2) on a per content basis.

1. **Globally**: This is done by passing the `canCloseContents` parameter to the `registerToolWindow()` function, or by
   specifying `canCloseContents="true"` in `plugin.xml`. The default value is `false`. Note that calling
   `setClosable(true)` on `ContentManager` content will be ignored unless `canCloseContents` is explicitly set.
2. **Per content basis**: This is done by calling `setCloseable(Boolean)` on each content object itself.

If closing tabs is enabled in general, a plugin can disable closing of specific tabs by calling
`Content.setCloseable(false)`.
