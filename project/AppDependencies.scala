import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.{ModuleID, _}

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"            % "0.68.0",
    "uk.gov.hmrc"       %% "logback-json-logger"           % "5.2.0",
    "uk.gov.hmrc"       %% "http-caching-client"           % "9.6.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.11.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"    % "6.2.0",
    "uk.gov.hmrc"       %% "play-allowlist-filter"         % "1.1.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"            % "6.2.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"          %% "scalatest"          % "3.2.12",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0",
    "org.scalatestplus"      %% "scalacheck-1-15"    % "3.2.11.0",
    "org.scalatestplus"      %% "mockito-3-12"       % "3.2.10.0",
    "org.jsoup"              %  "jsoup"              % "1.15.2",
    "com.typesafe.play"      %% "play-test"          % PlayVersion.current,
    "org.scalacheck"         %% "scalacheck"         % "1.16.0",
    "com.vladsch.flexmark"   %  "flexmark-all"       % "0.62.2"
  ).map(_ % "test")

  val all = compile ++ test
}
