import sbt.Keys._
import sbt.ScriptedPlugin.autoImport._
import sbt._

object Extra {

    val sbtPluginSettings = ScriptedPlugin.projectSettings ++ Seq(
        organization := "org.scala-native",
        version := "0.3.3-SNAPSHOT",
        sbtPlugin := true,
        scriptedLaunchOpts ++= Seq(
            "-Dplugin.version=" + version.value,
            "-Dplugin.sn-version=0.3.2",
            "-Dplugin.sjs-version=0.6.21"
        ),
        scalacOptions ++= Seq(
            "-deprecation",
            "-unchecked",
            "-feature",
            "-encoding",
            "utf8"
        )
    )

    lazy val publishSettings = Seq(
        publishArtifact in Compile := true,
        publishArtifact in Test := false,
        publishTo := {
            val corporateRepo = "http://toucan.simplesys.lan/"
            if (isSnapshot.value)
                Some("snapshots" at corporateRepo + "artifactory/libs-snapshot-local")
            else
                Some("releases" at corporateRepo + "artifactory/libs-release-local")
        },
        credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
    )

    lazy val noPublishSettings = Seq(
        publishArtifact := false,
        packagedArtifacts := Map.empty,
        publish := {},
        publishLocal := {}
    )

    private val createRootDoc = taskKey[File]("Generate ScalaDoc from README")

    lazy val scaladocFromReadme = Seq(
        createRootDoc := {
            // rootdoc.txt is the ScalaDoc landing page
            // we tweak the markdown so it's valid Scaladoc

            val readmeFile = (baseDirectory in ThisBuild).value / "README.md"
            val readme = IO.read(readmeFile)
            val scaladocReadme =
                readme
                  .replaceAllLiterally("```scala", "{{{")
                  .replaceAllLiterally("```", "}}}")

            val rootdoc = target.value / "rootdoc.txt"
            IO.delete(rootdoc)
            IO.write(rootdoc, scaladocReadme)
            rootdoc
        },
        scalacOptions in(Compile, doc) ++= Seq(
            "-doc-root-content",
            (target.value / "rootdoc.txt").getPath
        ),
        doc in Compile := (doc in Compile).dependsOn(createRootDoc).value
    )

    private lazy val duplicateProjectFoldersTask =
        taskKey[Unit]("Copy project folders in all scripted directories")

    val duplicateProjectFolders = Seq(
        duplicateProjectFoldersTask := {

            println("duplicating")

            val pluginsFileName = "plugins.sbt"
            val buildPropertiesFileName = "build.properties"
            val project = "project"

            val pluginsSbt = sbtTestDirectory.value / pluginsFileName
            val buildProperties = (baseDirectory in ThisBuild).value / project / buildPropertiesFileName

            val groups = sbtTestDirectory.value.listFiles.filter(_.isDirectory)
            val tests =
                groups.flatMap(_.listFiles).filterNot(_.name == "scala-native-only")

            tests.foreach { test =>
                val testProjectDir = test / project
                IO.createDirectory(testProjectDir)
                IO.copyFile(pluginsSbt, testProjectDir / pluginsFileName)
                IO.copyFile(buildProperties, testProjectDir / buildPropertiesFileName)
            }
        },
        scripted := scripted.dependsOn(duplicateProjectFoldersTask).evaluated
    )
}
