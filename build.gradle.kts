plugins {
  id("org.jetbrains.intellij") version "0.6.4"
  kotlin("jvm") version "1.4.10"
}

group = "com.developerlife.example"
version = "1.0"

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
  // Information on IJ versions https://www.jetbrains.org/intellij/sdk/docs/reference_guide/intellij_artifacts.html
  // You can use release build numbers or snapshot name for the version.
  // 1) IJ Release Repository w/ build numbers https://www.jetbrains.com/intellij-repository/releases/
  // 2) IJ EAP Snapshots Repository w/ snapshot names https://www.jetbrains.com/intellij-repository/snapshots/
  version = "203.5981-EAP-CANDIDATE-SNAPSHOT" // You can also use LATEST-EAP-SNAPSHOT here.

  // "java"
  // Declare a dependency on the Java plugin to be able to do Java language PSI access.
  // More info:
  // https://blog.jetbrains.com/platform/2019/06/java-functionality-extracted-as-a-plugin/
  //
  // "org.intellij.plugins.markdown"
  // Declare a dependency on the markdown plugin to be able to access the MarkdownRecursiveElementVisitor.kt file.
  //
  // More info:
  // JB docs on plugin dependencies: https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_dependencies.html
  // IDEA snapshots: https://www.jetbrains.com/intellij-repository/snapshots/
  // Markdown plugin snapshots: https://plugins.jetbrains.com/plugin/7793-markdown/versions
  //
  // The workflow to update to the latest version of IDEA and Markdown plugin goes something like this:
  // 1. Find the latest Markdown plugin release from the link above, and insert it below (replacing whatever version is
  //    there now). The webpage will also tell you which version of IDEA this is compatible w/.
  // 2. Find the IDEA snapshot that is compatible w/ the Markdown plugin above (which probably won't be the latest EAP
  //    snapshot). Replace the intellij.version (above) w/ this supported snapshot.
  setPlugins("java", "org.intellij.plugins.markdown:203.5981.37")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.languageVersion = "1.4"
    kotlinOptions.apiVersion = "1.4"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.languageVersion = "1.4"
    kotlinOptions.apiVersion = "1.4"
  }
}

tasks {
  runPluginVerifier {
    ideVersions(listOf<String>("2020.1.4", "2020.2.3", "2020.3"))
  }
}

tasks {
  patchPluginXml {
    setChangeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
  }
}

// Testing with JUnit4 and AssertJ.
// - To run tests from the command line, simply run: `./gradlew build test --info`
// - JUnit4 config info: https://docs.gradle.org/current/samples/sample_java_components_with_junit4_tests.html
// - No need to specify `sourceSets` for `test` since this project uses the default folder layout for gradle.
dependencies {
  testImplementation("org.assertj:assertj-core:3.11.1")
  testImplementation("junit:junit:4.13")
}

// Add color-console library.
repositories {
  jcenter()
}

dependencies {
  implementation("com.developerlife:color-console:1.0")
}
