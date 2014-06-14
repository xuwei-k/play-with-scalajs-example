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
      compile in Compile <<= (compile in Compile) dependsOn (fullOptJS in (scalajs, Compile)),
      dist <<= dist dependsOn (fullOptJS in (scalajs, Compile)),
      addSharedSrcSetting,
      libraryDependencies ++= (
        Nil
      )
    ) ++ (
      // ask scalajs project to put its outputs in scalajsOutputDir
      Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, fullOptJS) map { packageJSKey =>
        crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value
      }
    ) ++ commonSettings

  lazy val scalajsSettings =
    scalaJSSettings ++ Seq(
      name := "scalajs-example",
      version := "0.1.0-SNAPSHOT",
      libraryDependencies ++= (
        ("org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion % "test") ::
        ("org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6") ::
        ("org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6") ::
        Nil
      ),
      addSharedSrcSetting
    ) ++ commonSettings

  private val sourceFiles = SettingKey[Seq[Array[Byte]]]("sourceFiles")

  lazy val sharedScalaSettings =
    Seq(
      name := "shared-scala-example",
      scalaSource in Compile := baseDirectory.value,
      (sourceGenerators in Compile) <+= (sourceManaged in Compile).map(Boilerplate.gen),
      (sourceGenerators in Compile) <+= (sourceManaged in Compile).map(dir => Seq(GenerateTupleW(dir))),
      sourceFiles := {
        val sonatype = "https://oss.sonatype.org/content/repositories/releases/"
        def module(org: String, name: String, version: String) =
          sonatype + s"${org.replace('.', '/')}/$name/$version/$name-$version-sources.jar"
        Seq(
          module("io.argonaut", "argonaut_2.10", "6.0.4"),
          module("org.scalaz", "scalaz-core_2.10", "7.0.6")
        ).map{ jarURL =>
          IO.withTemporaryDirectory{ tmp =>
            val name = jarURL.split('/').last
            val jar = tmp / name
            println("downloading from " + jarURL)
            IO.download(url(jarURL), jar)
            println(s"$name is ${jar.length} bytes")
            IO.readBytes(jar)
          }
        }
      },
      (sourceGenerators in Compile) += {
        val dir = (sourceManaged in Compile).value
        task {
          sourceFiles.value.foreach{ jar =>
            IO.unzipStream(new java.io.ByteArrayInputStream(jar), dir, GlobFilter("*.scala"))
          }
          dir.***.get
        }
      }
    ) ++ commonSettings

  lazy val addSharedSrcSetting = unmanagedSourceDirectories in Compile += new File((baseDirectory.value / ".." / sharedSrcDir).getCanonicalPath)
}
