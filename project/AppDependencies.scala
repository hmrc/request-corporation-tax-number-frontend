import sbt.{ModuleID, *}

object AppDependencies {

  private val bootstrapVersion: String = "9.19.0"
  private val mongoVersion: String = "2.7.0"

  private val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                    % mongoVersion,
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30" % "3.3.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"            % "12.1.0"
  )

  private val test: Seq[ModuleID] = Seq(
    "org.scalatestplus"      %% "scalacheck-1-18"           % "3.2.19.0",
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"    % bootstrapVersion,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30"   % mongoVersion
  ).map(_ % "test")

  def apply(): Seq[ModuleID] = compile ++ test

}
