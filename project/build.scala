import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin.ScalateKeys._
import com.mojolly.scalate.ScalatePlugin._
import org.scalatra.sbt._
import sbt.Keys._
import sbt._

object OpenBlockchainBuild extends Build {
  lazy val phantomVersion = "1.22.0"
  lazy val cassandraVersion = "2.1.4"
  lazy val driverCore = "3.0.0"
  lazy val project = Project(
    "openblockchain",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers ++= Seq(
        "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
        "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
        "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
        "Sonatype repo" at "https://oss.sonatype.org/content/groups/scala-tools/",
        "Sonatype releases" at "https://oss.sonatype.org/content/org.dyne.danielsan.openblockchain.data.cassandra.init.repositories/releases",
        "Sonatype snapshots" at "https://oss.sonatype.org/content/org.dyne.danielsan.openblockchain.data.cassandra.init.repositories/snapshots",
        "Sonatype staging" at "http://oss.sonatype.org/content/org.dyne.danielsan.openblockchain.data.cassandra.init.repositories/staging",
        "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
        "Twitter Repository" at "http://maven.twttr.com",
        "Wedsudos Bintray Repo" at "https://dl.bintray.com/websudos/oss-releases/",
        "webjars" at "http://webjars.github.com/m2"
      ),
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
        "org.mockito" % "mockito-core" % "1.10.19",
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.json4s" %% "json4s-jackson" % ScalatraJacksonVersion,
        "org.scalaj" %% "scalaj-http" % "2.2.1",
        "org.scalatest" %% "scalatest" % "2.2.1" % "test",
        "org.scalacheck" %% "scalacheck" % "1.11.5" % "test",
        "com.websudos" %% "phantom-dsl" % phantomVersion,
        "com.websudos" %% "phantom-reactivestreams" % phantomVersion,
        "com.datastax.cassandra" % "cassandra-driver-core" % driverCore,
        "org.apache.cassandra" % "cassandra-all" % cassandraVersion
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile) { base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty, /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ), /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  ).enablePlugins(JettyPlugin)
  val Organization = "org.dyne.danielsan"
  val Name = "openblockchain"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.8"
  val ScalatraVersion = "2.4.0-RC2-2"
  val ScalatraJacksonVersion = "3.3.0.RC2"
}
