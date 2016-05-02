import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import scalariform.formatter.preferences._

name := "akka-http-extensions"

organization := "com.lonelyplanet"

version := "0.3.1"

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

bintrayOrganization := Some("lonelyplanet")

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

val doNotPublishSettings = Seq(publish := {})

val publishSettings =
  if (version.toString.endsWith("-SNAPSHOT"))
    Seq(
      publishTo := Some("Artifactory Realm" at "http://oss.jfrog.org/artifactory/oss-snapshot-local"),
      bintrayReleaseOnPublish := false,
      credentials := List(Path.userHome / ".bintray" / ".artifactory").filter(_.exists).map(Credentials(_))
    )
  else
    Seq(
      organization := "com.lonelyplanet",
      pomExtra := <scm>
        <url>https://github.com/lonelyplanet/akka-http-extensions</url>
        <connection>https://github.com/lonelyplanet/akka-http-extensions</connection>
      </scm>
        <developers>
          <developer>
            <id>wlk</id>
            <name>Wojciech Langiewicz</name>
            <url>https://github.com/lonelyplanet/akka-http-extensions</url>
          </developer>
        </developers>,
      publishArtifact in Test := false,
      homepage := Some(url("https://github.com/lonelyplanet/akka-http-extensions")),
      publishMavenStyle := false,
      resolvers += Resolver.url("lonelyplanet ivy resolver", url("http://dl.bintray.com/lonelyplanet/maven"))(Resolver.ivyStylePatterns)
    )