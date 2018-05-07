import sbtcrossproject.crossProject

lazy val old = crossProject
  .settings(scalaVersion := "2.12.5")
  .jsSettings(description := "js description")
  .jvmSettings(description := "jvm description")

lazy val oldJS = old.js
lazy val oldJVM = old.jvm
