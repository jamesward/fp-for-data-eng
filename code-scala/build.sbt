name := "fp-for-data-eng"

scalaVersion := "2.12.9"

libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.7"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.28"

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.192",
  "io.getquill" %% "quill-jdbc" % "3.4.3"
)

javaOptions += "-Dquill.binds.log=true"
