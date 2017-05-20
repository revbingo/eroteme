name := "quizly"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

resolvers += "Jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters
)

libraryDependencies += "com.github.kenglxn.qrgen" % "javase" % "2.2.0"

kotlinLib("stdlib")

routesGenerator := InjectedRoutesGenerator

LessKeys.compress in Assets := true

includeFilter in (Assets, LessKeys.less) := "*.less"

pipelineStages in Assets := Seq(uglify, gzip)