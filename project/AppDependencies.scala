import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.{ModuleID, _}

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"            % "0.51.0",
    "uk.gov.hmrc"       %% "logback-json-logger"           % "5.1.0",
    "uk.gov.hmrc"       %% "http-caching-client"           % "9.5.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.9.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"    % "5.7.0",
    "uk.gov.hmrc"       %% "play-allowlist-filter"          % "1.0.0-play-28",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"            % "0.82.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"          %% "scalatest"          % "3.1.4",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0",
    "org.scalatestplus"      %% "scalacheck-1-15"    % "3.2.9.0",
    "org.pegdown"            %  "pegdown"            % "1.6.0",
    "org.jsoup"              %  "jsoup"              % "1.14.1",
    "com.typesafe.play"      %% "play-test"          % PlayVersion.current,
    "org.mockito"            %  "mockito-core"       % "3.11.2",
    "org.scalacheck"         %% "scalacheck"         % "1.15.4",
    "com.vladsch.flexmark"    %  "flexmark-all"        % "0.35.10"
  ).map(_ % "test")

  val all = compile ++ test
}
