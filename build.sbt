import Extra._

lazy val `sbt-crossproject-root` =
  project
    .in(file("."))
    .aggregate(`sbt-scalajs-crossproject`,
               `sbt-crossproject`,
               `sbt-crossproject-test`)
    .dependsOn(`sbt-scalajs-crossproject`,
               `sbt-crossproject`,
               `sbt-crossproject-test`)
    .settings(noPublishSettings)

lazy val `sbt-scalajs-crossproject` =
  project
    .in(file("sbt-scalajs-crossproject"))
    .settings(sbtPluginSettings)
    .settings(
      moduleName := "sbt-scalajs-crossproject",
      /* Work around https://github.com/sbt/sbt/issues/3393.
       * Should be:
       *   addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.19")
       * We inline the fixed definition of addSbtPlugin to be
       * released with sbt 0.13.17.
       */
      libraryDependencies += {
        val sbtV   = (sbtBinaryVersion in pluginCrossBuild).value
        val scalaV = (scalaBinaryVersion in update).value
        Defaults.sbtPluginExtra("org.scala-js" % "sbt-scalajs" % "0.6.19",
                                sbtV,
                                scalaV)
      }
    )
    .settings(publishSettings)
    .enablePlugins(BintrayPlugin)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-crossproject` =
  project
    .in(file("sbt-crossproject"))
    .settings(moduleName := "sbt-crossproject")
    .settings(sbtPluginSettings)
    .settings(scaladocFromReadme)
    .settings(publishSettings)
    .enablePlugins(BintrayPlugin)

lazy val `sbt-crossproject-test` =
  project
    .in(file("sbt-crossproject-test"))
    .settings(sbtPluginSettings)
    .settings(noPublishSettings)
    .settings(
      scripted := scripted
        .dependsOn(
          publishLocal in `sbt-crossproject`,
          publishLocal in `sbt-scalajs-crossproject`
        )
        .evaluated
    )
    .settings(duplicateProjectFolders)
