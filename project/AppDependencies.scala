import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.{ModuleID, _}

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-27"            % "0.50.0",
    "uk.gov.hmrc"       %% "logback-json-logger"           % "5.1.0",
    "uk.gov.hmrc"       %% "play-health"                   % "3.16.0-play-27",
    "uk.gov.hmrc"       %% "http-caching-client"           % "9.5.0-play-27",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.9.0-play-27",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-27"    % "5.3.0",
    "uk.gov.hmrc"       %% "play-allowlist-filter"          % "1.0.0-play-27",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"            % "0.69.0-play-27"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"          %% "scalatest"          % "3.0.9",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3",
    "org.pegdown"            %  "pegdown"            % "1.6.0",
    "org.jsoup"              %  "jsoup"              % "1.13.1",
    "com.typesafe.play"      %% "play-test"          % PlayVersion.current,
    "org.mockito"            %  "mockito-core"       % "3.11.0",
    "org.scalacheck"         %% "scalacheck"         % "1.15.4"
  ).map(_ % "test")

  val all = compile ++ test
}
