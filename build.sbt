name := "avro2caseclass"

version := "0.0.2"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(play.PlayScala).settings()

libraryDependencies ++= Seq(
    jdbc,
    anorm,
    cache,
    "org.webjars" %% "webjars-play" % "2.3.0-3", 
    "org.webjars" % "bootstrap" % "2.3.0",
    "org.webjars" % "font-awesome" % "4.3.0-1",
    "com.julianpeeters" %% "avrohugger-core" % "0.5.1",
    "se.radley" %% "play-plugins-salat" % "1.5.0",
    "com.novus" %% "salat" % "1.9.9"
)    


PlayKeys.routesImport += "se.radley.plugin.salat.Binders._"

TwirlKeys.templateImports += "org.bson.types.ObjectId"

// needed for large schemas
javaOptions in run += "-Dhttp.netty.maxInitialLineLength=8192"

