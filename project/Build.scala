import sbt._
import Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import com.typesafe.sbt.packager.universal.UniversalKeys

object ApplicationBuild extends Build with UniversalKeys {

  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  override def rootProject = Some(scalajvm)

  val commonSettings = Seq(
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    scalacOptions ++= (
      if(scalaVersion.value startsWith "2.11")
        Seq("-Ywarn-unused", "-Ywarn-unused-import")
      else
        Nil
    )
  )

  val sharedSrcDir = "scala"

  lazy val scalajvm = Project(
    "scalajvm",
    file("scalajvm")
  ).enablePlugins(play.PlayScala) settings (scalajvmSettings: _*) aggregate (scalajs)

  lazy val scalajs = Project(
    "scalajs",
    file("scalajs")
  ) settings (scalajsSettings: _*)

  lazy val sharedScala = Project(
    "sharedScala",
    file(sharedSrcDir)
  ) settings(sharedScalaSettings: _*)

  lazy val scalajvmSettings =
    Seq(
      name                 := "play-example",
      version              := "0.1.0-SNAPSHOT",
      scalajsOutputDir     := (crossTarget in Compile).value / "classes" / "public" / "javascripts",
      compile in Compile <<= (compile in Compile) dependsOn (preoptimizeJS in (scalajs, Compile)),
      dist <<= dist dependsOn (optimizeJS in (scalajs, Compile)),
      addSharedSrcSetting,
      libraryDependencies ++= (
        Nil
      )
    ) ++ (
      // ask scalajs project to put its outputs in scalajsOutputDir
      Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, preoptimizeJS, optimizeJS) map { packageJSKey =>
        crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value
      }
    ) ++ commonSettings

  lazy val scalajsSettings =
    scalaJSSettings ++ Seq(
      name := "scalajs-example",
      version := "0.1.0-SNAPSHOT",
      libraryDependencies ++= Seq(
        "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion % "test",
        "org.scala-lang.modules.scalajs" %% "scalajs-dom" % "0.4"
      ),
      addSharedSrcSetting
    ) ++ commonSettings

  lazy val sharedScalaSettings =
    Seq(
      name := "shared-scala-example",
      scalaSource in Compile := baseDirectory.value
    ) ++ commonSettings

  lazy val addSharedSrcSetting = unmanagedSourceDirectories in Compile += new File((baseDirectory.value / ".." / sharedSrcDir).getCanonicalPath)
}
