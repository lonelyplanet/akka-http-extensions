import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import scalariform.formatter.preferences._

name := "akka-http-extensions"

version := "0.1"

scalaVersion := "2.11.8"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-unchecked",
  "-deprecation"
)


resolvers ++= Seq("OSS" at "http://oss.sonatype.org/content/repositories/releases/")


libraryDependencies ++= {
  val akkaVersion = "2.3.15" //downgraded because of akkaStream requirements/compatibility
  val akkaStreamVersion = "2.0.4"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion % "provided",
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamVersion % "provided",
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamVersion % "provided",
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamVersion % "provided",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamVersion % "provided"
  )
}


SbtScalariform.scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(SpacesAroundMultiImports, false)
  .setPreference(CompactControlReadability, false)