
name := """challengeAuthoringSystemServer"""
organization := "ch.hsr"

version := "0.0.1"

scalaVersion := "2.12.3"

// https://github.com/sbt/junit-interface
testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-deprecation", "-Xlint")

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

routesImport += "binders.MediaQueryString"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += javaJdbc
libraryDependencies += ehcache
libraryDependencies += "mysql" % "mysql-connector-java" % "6.0.6"

libraryDependencies += "com.auth0" % "java-jwt" % "3.2.0"

libraryDependencies += "org.pac4j" % "play-pac4j" % "4.0.0"
libraryDependencies += "org.pac4j" % "pac4j-oidc" % "2.1.0"

libraryDependencies += "org.jsoup" % "jsoup" % "1.11.2"

libraryDependencies += "org.bitbucket.leito" % "universal-document-converter" % "1.1.0"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.8.0" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "3.0.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0" % "test"
testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")

LessKeys.compress := true


//PlayKeys.externalizeResources := false
