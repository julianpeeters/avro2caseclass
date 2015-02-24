name := "avro2caseclass"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.5"

fork in run := true

lazy val root = (project in file(".")).enablePlugins(play.PlayScala).settings()

libraryDependencies ++= Seq(
    "org.webjars" %% "webjars-play" % "2.3.0-3", 
    "org.webjars" % "bootstrap" % "3.1.0",
    "com.julianpeeters" %% "avrohugger-core" % "0.1.0-SNAPSHOT"   

)     

// needed for large schemas
javaOptions in run += "-Dhttp.netty.maxInitialLineLength=8192"