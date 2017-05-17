name := "quizly"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters
)

kotlinLib("stdlib")

routesGenerator := InjectedRoutesGenerator

LessKeys.compress in Assets := true

includeFilter in (Assets, LessKeys.less) := "*.less"

pipelineStages in Assets := Seq(uglify, gzip)