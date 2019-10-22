
lazy val akkaVersion = "2.5.26"
lazy val akkaTypedVersion = "2.5.26" //"2.5.7"
enablePlugins(ProtobufPlugin)

val assemblyJarName = taskKey[String]("Evaluation")
lazy val `zio-version` = "1.0.0-RC15"
lazy val `zio-interop` = "2.0.0.0-RC6"
lazy val zio = "dev.zio" %% "zio" %  `zio-version`
lazy val `zio-test` = "dev.zio" %% "zio-test" % `zio-version` % "test"
lazy val `zio-test-sbt` = "dev.zio" %% "zio-test-sbt" % `zio-version` % "test"
lazy val `zio-interop-shared` = "dev.zio" %% "zio-interop-shared" % `zio-version`
lazy val `zio-interop-cats` = "dev.zio" %% "zio-interop-cats" % `zio-interop`
lazy val root = (project in file(".")).
  settings(
    mainClass in assembly := Some("com.iws.main.WebServer"),
    // more settings here ...
  ).
  settings( 
    inThisBuild(List(
      organization    := "com.iws",
      scalaVersion    := "2.12.9"
    )) 
   ,
    name := "Evaluation",
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.csv" % "0.5.1",
       "com.nrinaudo" %% "kantan.csv-generic" % "0.5.1",
      "com.typesafe.akka" %% "akka-actor-typed" % akkaTypedVersion,
      "com.typesafe.akka" %% "akka-http"   % "10.1.10", //"10.1.3",
      "com.typesafe.akka" %% "akka-stream" % "2.5.26", //"2.5.12",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",
      "io.spray" %%  "spray-json" % "1.3.4",
      //"com.github.mlangc"  %% "slf4zio" % "0.2.1",
      //"com.nequissimus" %% "zio-slf4j" % "0.3.0",
      "dev.zio" %% "zio" %  `zio-version`,
      "dev.zio" %% "zio-test" % `zio-version` % "test",
      "dev.zio" %% "zio-test-sbt" % `zio-version` % "test"
     // "dev.zio" %% "zio-interop-cats" % `zio-interop`
    )
  )
