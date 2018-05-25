import sbtcrossproject.{crossProject, CrossType}

lazy val bar = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(scalaVersion := "2.12.")

lazy val barJS  = bar.js
lazy val barJVM = bar.jvm

lazy val buzz = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(scalaVersion := "2.12.")

lazy val buzzJVM    = buzz.jvm
lazy val buzzNative = buzz.native

lazy val foo = crossProject(JSPlatform, NativePlatform)
  .settings(scalaVersion := "2.12.")
  .aggregate(bar, buzz)

lazy val fooJS = foo.js
lazy val fooNative = foo.native
