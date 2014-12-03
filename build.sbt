lazy val root = (project in file(".")).
	aggregate(utility,center,finder,node,user)

lazy val utility = project.in(file("utility")).
	settings(
		name := "utility",
		version := "1.0",
		scalaVersion := "2.11.4",
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
		)
)

lazy val center = project.in(file("center")).dependsOn(utility).
	settings(
		name := "center",
		version := "1.0",
		scalaVersion := "2.11.4",
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"		
		)
)

lazy val node = project.in(file("node")).dependsOn(utility).
	settings(
		name := "node",
		version := "1.0",
		scalaVersion := "2.11.4",
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"		
		)
)

lazy val user = project.in(file("user")).dependsOn(utility).
	settings(
		name := "user",
		version := "1.0",
		scalaVersion := "2.11.4",
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5", 
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"		
		)
)

lazy val finder = project.in(file("finder")).dependsOn(utility).
	settings(
		name := "finder",
		version := "1.0",
		scalaVersion := "2.11.4",
		libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.3.7",
			"com.typesafe" % "config" % "1.2.1",
			"joda-time" % "joda-time" % "2.5",
			"org.joda" % "joda-convert" % "1.2",
			"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
		)
)
