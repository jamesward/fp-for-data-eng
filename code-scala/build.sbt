name := "fp-for-data-eng"

scalaVersion := "2.12.9"

libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.7"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.2"

libraryDependencies ++= Seq(
  "com.mayreh" %% "scalikejdbc-bigquery" % "0.1.3",
  "com.google.cloud" % "google-cloud-bigquery" % "1.88.0",
  "org.scalikejdbc" %% "scalikejdbc" % "3.3.5"
)
