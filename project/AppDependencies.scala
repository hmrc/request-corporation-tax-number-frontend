import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.{ModuleID, _}

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "simple-reactivemongo" % "7.26.0-play-26",
    "uk.gov.hmrc" %% "logback-json-logger" % "4.8.0",
    "uk.gov.hmrc" %% "govuk-template" % "5.55.0-play-26",
    "uk.gov.hmrc" %% "play-health" % "3.15.0-play-26",
    "uk.gov.hmrc" %% "play-ui" % "8.10.0-play-26",
    "uk.gov.hmrc" %% "http-caching-client" % "9.0.0-play-26",
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "1.2.0-play-26",
    "uk.gov.hmrc" %% "bootstrap-play-26" % "1.8.0",
    "uk.gov.hmrc" %% "play-language" % "4.3.0-play-26",
    "uk.gov.hmrc" %% "play-whitelist-filter" % "3.4.0-play-26"
  )

  val test: Seq[ModuleID] = Seq(

    "uk.gov.hmrc" %% "hmrctest" % "3.9.0-play-26",
    "org.scalatest" %% "scalatest" % "3.0.8",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3",
    "org.pegdown" % "pegdown" % "1.6.0",
    "org.jsoup" % "jsoup" % "1.13.1",
    "com.typesafe.play" %% "play-test" % PlayVersion.current,
    "org.mockito" % "mockito-core" % "3.3.3",
    "org.scalacheck" %% "scalacheck" % "1.14.3"
  ).map(_ % "test")

  val all = compile ++ test
}
