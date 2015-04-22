name := "quizly"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies ++= Seq(
  javaJdbc,
  cache
)

LessKeys.compress in Assets := true

includeFilter in (Assets, LessKeys.less) := "*.less"

pipelineStages in Assets := Seq(uglify)