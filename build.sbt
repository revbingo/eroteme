name := "quizly"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies ++= Seq(
  "com.beowulfe.play" % "ebean-jdk8-fix" % "3.3.1-SNAPSHOT",
  javaJdbc,
  javaEbean,
  cache
)

pipelineStages in Assets := Seq(uglify)