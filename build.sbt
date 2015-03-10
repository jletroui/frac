name := "frac"

version := "1.0.5"

organization := "ca.frac"

scalaVersion := "2.11.6"

scalacOptions := Seq("-encoding", "utf8")

crossPaths := false

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing" % "1.0.1" % "compile",
  "org.parboiled" %% "parboiled-scala" % "1.1.7" % "compile",
  "org.specs2" %% "specs2-core" % "3.0.1" % "test"
)

mainClass in assembly := Some("frac.Main")

jarName in assembly := s"${name.value}-${version.value}.jar"

