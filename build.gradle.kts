import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.intellij") version "0.4.16"
    kotlin("jvm") version "1.3.70"
}

group = "com.developerlife.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

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

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}

// Testing with JUnit4 and AssertJ.
// - To run tests from the command line, simply run: `./gradlew build test --info`
// - JUnit4 config info: https://docs.gradle.org/current/samples/sample_java_components_with_junit4_tests.html
// - No need to specify `sourceSets` for `test` since this project uses the default folder layout for gradle.
dependencies {
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("junit:junit:4.13")
}
