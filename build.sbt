name := "frac"

version := "1.0.6"

organization := "ca.frac"

scalaVersion := "2.12.8"

scalacOptions := Seq("-encoding", "utf8")

crossPaths := false

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing" % "2.1.1" % "compile",
  "org.parboiled" %% "parboiled-scala" % "1.3.0" % "compile",
  "org.specs2" %% "specs2-core" % "4.3.4" % "test"
)

mainClass in assembly := Some("frac.Main")

jarName in assembly := s"${name.value}-${version.value}.jar"
