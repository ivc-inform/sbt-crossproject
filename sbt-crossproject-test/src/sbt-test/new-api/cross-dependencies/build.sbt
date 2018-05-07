import sbtcrossproject.{crossProject, CrossType}

val g = "org.example.cross-dependencies"
val a = "bar"
val v = "0.1.0"

lazy val bar = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(
    scalaVersion := "2.12.5",
    organization := g,
    moduleName := a,
    version := v
  )

lazy val barJS     = bar.js
lazy val barJVM    = bar.jvm
lazy val barNative = bar.native

lazy val foo = crossProject(JSPlatform, JVMPlatform, NativePlatform).settings(
  scalaVersion := "2.12.5",
  libraryDependencies += g %%% a % v
)

lazy val fooJS = foo.js
lazy val fooJVM = foo.jvm
lazy val fooNative = foo.native
