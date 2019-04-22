resolvers in Global ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayRepo("io-monadplus", "maven")
)

lazy val contributors = Seq(
  "monadplus" -> "Arnau Abella"
)

val catsV = "1.6.0"
val kittensV = "1.2.0"
val catsEffectV = "1.2.0"
val mouseV = "0.20"
val catsMtlV = "0.4.0"
val fs2V = "1.0.3"
val http4sV = "0.20.0-RC1"
val circeV = "0.11.1"
val doobieV = "0.6.0"
val pureConfigV = "0.10.2"
val equalityV = "0.0.2"
val monocleV = "1.5.1-cats"
val shapelessV = "2.3.3"
val refinedV = "0.9.5"
val simulacrumV = "0.16.0"
val catsParV = "0.2.1"
val catsTimeV = "0.2.0"
val fuuidV = "0.2.0-M8"
val lineBackerV = "0.2.0"
val meowMtlV = "0.2.1"

val scalaTestV = "3.0.5"
val scalaCheckV = "1.14.0"

val kindProjectorV = "0.9.9"
val betterMonadicForV = "0.3.0-M4"

lazy val commonDependencies = Seq(
  "org.typelevel" %% "cats-macros" % catsV,
  "org.typelevel" %% "cats-kernel" % catsV,
  "org.typelevel" %% "cats-core" % catsV,
  "org.typelevel" %% "cats-laws" % catsV,
  "org.typelevel" %% "cats-free" % catsV,
  "org.typelevel" %% "cats-testkit" % catsV % Test,
  "org.typelevel" %% "alleycats-core" % catsV, // Cats instances and class which are not lawful

  "org.typelevel" %% "kittens" % kittensV, // auto-derivation for cats

  "org.typelevel" %% "mouse" % mouseV,
  "com.olegpy" %% "meow-mtl" % meowMtlV,

  "org.typelevel" %% "cats-mtl-core" % catsMtlV,

  "com.chuusai" %% "shapeless" % shapelessV,

  "org.typelevel" %% "cats-effect" % catsEffectV,

  "co.fs2" %% "fs2-core" % fs2V,
  "co.fs2" %% "fs2-io" % fs2V,

  "io.chrisdavenport" %% "cats-par" % catsParV,
  "io.chrisdavenport" %% "cats-time" % catsTimeV,
  "io.chrisdavenport" %% "linebacker" % lineBackerV,
  "io.chrisdavenport" %% "fuuid" % fuuidV,

  "org.http4s" %% "http4s-dsl" % http4sV,
  "org.http4s" %% "http4s-blaze-server" % http4sV,
  "org.http4s" %% "http4s-blaze-client" % http4sV,
  "org.http4s" %% "http4s-circe" % http4sV,

  "io.circe" %% "circe-core" % circeV,
  "io.circe" %% "circe-generic" % circeV,
  "io.circe" %% "circe-parser" % circeV,
//  "io.circe" %% "circe-fs2" % circeV,

  "org.tpolecat" %% "doobie-core" % doobieV,
  "org.tpolecat" %% "doobie-h2" % doobieV,
  "org.tpolecat" %% "doobie-hikari" % doobieV,
  "org.tpolecat" %% "doobie-postgres" % doobieV,
  "org.tpolecat" %% "doobie-scalatest" % doobieV % Test,

  "com.github.pureconfig" %% "pureconfig" % pureConfigV,

  "io.monadplus" %% "equality-core" % equalityV,

  "com.github.mpilquist" %% "simulacrum" % simulacrumV,

  "com.github.julien-truffaut" %%  "monocle-core"  % monocleV,
  "com.github.julien-truffaut" %%  "monocle-macro" % monocleV,
  "com.github.julien-truffaut" %%  "monocle-refined"   % monocleV,
  "com.github.julien-truffaut" %%  "monocle-law"   % monocleV % Test,

  "eu.timepit" %% "refined" % refinedV,
  "eu.timepit" %% "refined-scalacheck" % refinedV % Test,

  "com.github.mpilquist" %% "simulacrum" % simulacrumV,

  "org.scalatest" %% "scalatest" % scalaTestV % Test,

  "org.scalacheck" %% "scalacheck" % scalaCheckV % Test
)
//simulacrumV=0.12.0

lazy val compilerFlags = Seq(
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-language:higherKinds"
  ) ++ (if (scalaBinaryVersion.value.startsWith("2.12"))
          List(
            "-Xlint",
            "-Xfatal-warnings",
            "-Yno-adapted-args",
            "-Ywarn-value-discard",
            "-Ywarn-unused-import",
            "-Ypartial-unification"
          )
        else Nil),
  scalacOptions in (Test, compile) --= Seq(
    "-Ywarn-unused-import",
    "-Xlint",
    "-Xfatal-warnings"
  )
)
lazy val commonSettings = Seq(
  organization := "io.monadplus",
  scalaVersion := "2.12.8",
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  description := "TODO",
  parallelExecution in Test := true,
  fork in Test := true,
  addCompilerPlugin("org.spire-math" % "kind-projector" % kindProjectorV cross CrossVersion.binary),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForV),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  libraryDependencies ++= commonDependencies
)

lazy val `scala-lens` = project
  .in(file("."))
  .settings(commonSettings)
  .aggregate(core)

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "scala-lens"
  )
