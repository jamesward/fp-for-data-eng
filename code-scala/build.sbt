name := "fp-for-data-eng"

scalaVersion := "2.12.9"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.28"

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.192",
  "io.getquill" %% "quill-jdbc" % "3.4.3"
)

libraryDependencies ++= Seq(
  "org.apache.flink" %% "flink-streaming-scala" % "1.9.0",
  "com.typesafe.play" %% "play-json" % "2.7.3",
  "com.typesafe.akka" %% "akka-actor" % "2.5.25",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.25",
  "com.typesafe.akka" %% "akka-stream" % "2.5.25",
  "com.typesafe.akka" %% "akka-http" % "10.1.9",
)

javaOptions += "-Dquill.binds.log=true"
