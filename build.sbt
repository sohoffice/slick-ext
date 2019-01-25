
inThisBuild(
  Seq(
    version in ThisBuild := "0.0.5",
    organization in ThisBuild := "com.sohoffice"
  )
)

lazy val bintrayCommonSettings = Seq(
  publishMavenStyle := false,
  licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
  bintrayVcsUrl := Some("git@github.com:sohoffice/slick-ext.git"),
  bintrayRepository := "releases",
  bintrayOrganization in bintray := None
)

name := "slick-ext"

scalaVersion := "2.12.6"

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.2" withSources()
libraryDependencies += "net.postgis" % "postgis-jdbc" % "2.2.1" withSources()
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.0" withSources()
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0" withSources()
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.8" withSources()

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25" % "test" withSources()
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % "test" withSources()
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.3" % "test" withSources()

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test" withSources()
libraryDependencies += "com.typesafe" % "config" % "1.3.2" % "test" withSources()

bintrayCommonSettings