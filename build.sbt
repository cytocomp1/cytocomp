lazy val root = (project in file(".")).
  settings(
    name := "mtt",
    version := "1.0",
    scalaVersion := "2.11.8"
  )
mainClass in (Compile, run) := Some("com.cytocomp.mtt.MTTApp")

libraryDependencies += "org.sbml.jsbml" % "jsbml" % "1.2"
// Required by jsbml:
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.7"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.7"

libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.8.0"

//libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3"

libraryDependencies += "org.tinyjee.jgraphx" % "jgraphx" % "1.10.1.3"

libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"

libraryDependencies += "net.sf.py4j" % "py4j" % "0.10.6"