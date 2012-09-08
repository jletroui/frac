import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object FracBuild extends Build {
  lazy val frac = Project(
      id = "frac",
      base = file(".")
    )
    .settings(assemblySettings :_*)
    .settings(
      version := "1.0",
      organization := "ca.frac",
      scalaVersion := V.scala,
      scalacOptions := Seq("-encoding", "utf8"),
      crossPaths := false,
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-swing" % V.scala % "compile",
        "org.specs2" %% "specs2" % V.specs2 % "test"
      ),
      mainClass in assembly := Some("frac.Main")
    )

  object V {
    val scala = "2.9.1"
    val specs2 = "1.12.1"
  }

}
