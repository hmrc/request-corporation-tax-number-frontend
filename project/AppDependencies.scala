import sbt.{ModuleID, *}

object AppDependencies {

  private val bootstrapVersion: String = "7.23.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"            % "1.5.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.13.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"    % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-allowlist-filter"         % "1.2.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-28"    % "8.5.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"          %% "scalatest"              % "3.2.18",
    "org.scalatestplus"      %% "mockito-5-10"           % "3.2.18.0",
    "org.scalatestplus"      %% "scalacheck-1-17"        % "3.2.18.0",
    "org.jsoup"              %  "jsoup"                  % "1.17.2",
    "org.scalacheck"         %% "scalacheck"             % "1.17.0",
    "com.vladsch.flexmark"   %  "flexmark-all"           % "0.64.8",
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapVersion
  ).map(_ % "test")

  val all: Seq[ModuleID] = compile ++ test
}
