
lazy val Common = project.in(file("Common")).
	settings(
		name := "Common",
		version := "1.0",
		scalaVersion := "2.11.4",
		exportJars := true,
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
		)
)

lazy val ClusterManager = project.in(file("ClusterManager")).dependsOn(Common).
	settings(
		name := "ClusterManager",
		version := "1.0",
		scalaVersion := "2.11.4",
		exportJars := true,
		mainClass in (Compile,run):= Some("ca.usask.agents.macrm.clustermanager.agents.main"),
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe.akka" % "akka-remote_2.11" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
			"com.typesafe.akka" %% "akka-camel" % "2.3.7",
			"org.apache.camel" % "camel-netty" % "2.14.1",
			"org.apache.camel" % "camel-jetty" % "2.14.1",
			"org.slf4j" % "slf4j-simple" % "1.7.10",
			"com.typesafe.play" % "play-json_2.11" % "2.4.0-M2"
		)
)


lazy val JobManager = project.in(file("JobManager")).dependsOn(Common).
	settings(
		name := "JobManager",
		version := "1.0",
		scalaVersion := "2.11.4",
		exportJars := true,
		mainClass in (Compile,run):= Some("ca.usask.agents.macrm.jobmanager.agents.main"),
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe.akka" % "akka-remote_2.11" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5", 
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"		
		)
)

lazy val NodeManager = project.in(file("NodeManager")).dependsOn(Common,JobManager).
	settings(
		name := "NodeManager",
		version := "1.0",
		scalaVersion := "2.11.4",
		exportJars := true,
		mainClass in (Compile,run):= Some("ca.usask.agents.macrm.nodemanager.agents.main"),
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe.akka" % "akka-remote_2.11" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"		
		)
)

lazy val ResourceTracker = project.in(file("ResourceTracker")).dependsOn(Common).
	settings(
		name := "ResourceTracker",
		version := "1.0",
		scalaVersion := "2.11.4",
		exportJars := true,
		mainClass in (Compile,run):= Some("ca.usask.agents.macrm.resourcetracker.agents.main"),
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe.akka" % "akka-remote_2.11" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
		)
)
