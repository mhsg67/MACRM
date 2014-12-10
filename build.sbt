
lazy val common = project.in(file("common")).
	settings(
		name := "common",
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

lazy val center = project.in(file("center")).dependsOn(common).
	settings(
		name := "center",
		version := "1.0",
		scalaVersion := "2.11.4",
		exportJars := true,
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe.akka" % "akka-remote_2.11" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"		
		)
)

lazy val node = project.in(file("node")).dependsOn(common).
	settings(
		name := "node",
		version := "1.0",
		scalaVersion := "2.11.4",
		exportJars := true,
		mainClass := Some("ca.usask.agents.macrm.node.agents.main"),
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe.akka" % "akka-remote_2.11" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"		
		)
)

lazy val user = project.in(file("user")).dependsOn(common).
	settings(
		name := "user",
		version := "1.0",
		scalaVersion := "2.11.4",
		exportJars := true,
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe.akka" % "akka-remote_2.11" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5", 
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"		
		)
)

lazy val tracking = project.in(file("tracking")).dependsOn(common).
	settings(
		name := "tracking",
		version := "1.0",
		scalaVersion := "2.11.4",
		exportJars := true,
		mainClass in (Compile,run):= Some("ca.usask.agents.macrm.tracker.agents.Main"),
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe.akka" % "akka-remote_2.11" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
		)
)
