name := "proxy"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies := Seq(
    "com.twitter" % "finagle-http_2.12" % "17.11.0",
    "com.twitter" % "finagle-http2_2.12" % "17.11.0"
)