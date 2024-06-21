import sbt.{ModuleID, *}

object AppDependencies {

  private val bootstrapVersion: String = "8.6.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"            % "1.9.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30" % "2.0.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"    % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-allowlist-filter"         % "1.3.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"    % "9.11.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"          %% "scalatest"              % "3.2.18",
    "org.scalatestplus"      %% "mockito-5-10"           % "3.2.18.0",
    "org.scalatestplus"      %% "scalacheck-1-17"        % "3.2.18.0",
    "org.jsoup"              %  "jsoup"                  % "1.17.2",
    "org.scalacheck"         %% "scalacheck"             % "1.18.0",
    "com.vladsch.flexmark"   %  "flexmark-all"           % "0.64.8",
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootstrapVersion
  ).map(_ % "test")

  val all: Seq[ModuleID] = compile ++ test
}
