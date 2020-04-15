# idea-plugin-template

This is a starter project to help you quickly create a new IDEA plugin that uses gradle, and not DevKit. Here are some
features that come with this plugin:

1. Setup to run JUnit4 (using AssertJ) tests
2. Can run tests from command line w/ gradle, and from run configuration named "run all tests"
3. Has run configuration called "runIde" which runs the gradle task to launch a test IDE
4. Has utility classes for testing as well as plugin code in Kotlin
5. Can load testdata from the testdata folder using TestUtils.kt
6. `.gitignore` and `.prettierrc` have already been setup and configured

In order to use this project, search for the strings "template" (your new IDEA project name) and "com.developerlife"
(your new package name) across the entire project folder. You will find these pop up in the following locations (which
are relative to PROJECT_DIR).

Project name updates:

1. `settings.gradle.kts` - change "template" to the name of your new IDEA project
2. `.idea/.name` - change "template" to the name of your new IDEA project
3. `.idea/modules.xml` - change "template" to the name of your new IDEA project
4. `src/main/resources/META-INF/plugin.xml` - change "template" to the name of your new IDEA project

Package name updates:

1. `build.gradle.kts` - change "com.developerlife" to the package of your new IDEA project
2. `src/main/resources/META-INF/plugin.xml` - change "com.developerlife" to the package of your new IDEA project

Change the file names of the .iml files:

1. Rename the two .iml files in `.idea/modules/` folder: `template.main.iml` and `template.test.iml`.
2. Change "template" to the name of your new IDEA project (and this should match the changes you made in the
   `./idea/modules.xml` file).
