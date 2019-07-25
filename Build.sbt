import play.core.PlayVersion
import play.sbt.routes.RoutesKeys
import sbt.Tests.{Group, SubProcess}
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, defaultSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import uk.gov.hmrc.{SbtArtifactory, SbtAutoBuildPlugin}

val appName = "request-corporation-tax-number-frontend"

lazy val appDependencies: Seq[ModuleID] = compile ++ test()
lazy val plugins : Seq[Plugins] = Seq.empty
lazy val playSettings : Seq[Setting[_]] = Seq.empty

val playHealthVersion = "3.10.0-play-25"
val logbackJsonLoggerVersion = "4.3.0"
val govukTemplateVersion = "5.27.0-play-25"
val playUiVersion = "7.31.0-play-25"
val hmrcTestVersion = "3.4.0-play-25"
val scalaTestVersion = "3.0.5"
val scalaTestPlusPlayVersion = "2.0.1"
val pegdownVersion = "1.6.0"
val mockitoAllVersion = "1.10.19"

val httpCachingClientVersion = "8.0.0"
val playReactivemongoVersion = "6.2.0"
val playConditionalFormMappingVersion = "0.2.0"
val playLanguageVersion = "4.0.0"
val bootstrapVersion = "4.13.0"
val scalacheckVersion = "1.14.0"
val whitelistVersion = "2.0.0"

val compile = Seq(
  ws,
  "uk.gov.hmrc" %% "play-reactivemongo" % playReactivemongoVersion,
  "uk.gov.hmrc" %% "logback-json-logger" % logbackJsonLoggerVersion,
  "uk.gov.hmrc" %% "govuk-template" % govukTemplateVersion,
  "uk.gov.hmrc" %% "play-health" % playHealthVersion,
  "uk.gov.hmrc" %% "play-ui" % playUiVersion,
  "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
  "uk.gov.hmrc" %% "play-conditional-form-mapping" % playConditionalFormMappingVersion,
  "uk.gov.hmrc" %% "bootstrap-play-25" % bootstrapVersion,
  "uk.gov.hmrc" %% "play-language" % playLanguageVersion,
  "uk.gov.hmrc" %% "play-whitelist-filter" % whitelistVersion
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
  "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % scope,
  "org.pegdown" % "pegdown" % pegdownVersion % scope,
  "org.jsoup" % "jsoup" % "1.11.3" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.mockito" % "mockito-all" % mockitoAllVersion % scope,
  "org.scalacheck" %% "scalacheck" % scalacheckVersion % scope
)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]) =
  tests map {
    test => new Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
  }

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory) ++ plugins : _*)
  .settings(playSettings : _*)
  .settings(RoutesKeys.routesImport ++= Seq("models._"))
  .settings(
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*models.*;.*repositories.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*DataCacheConnector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController",
    ScoverageKeys.coverageMinimum := 75,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    scalacOptions ++= Seq("-Xfatal-warnings", "-feature"),
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest)(base => Seq(base / "it")),
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false)
  .settings(resolvers ++= Seq(
    Resolver.bintrayRepo("hmrc", "releases"),
    Resolver.jcenterRepo,
    "hmrc-releases" at "https://artefacts.tax.service.gov.uk/artifactory/hmrc-releases/",
    Resolver.bintrayRepo("emueller", "maven")))
  .settings(
    // concatenate js
    Concat.groups := Seq(
      "javascripts/requestcorporationtaxnumberfrontend-app.js" -> group(Seq("javascripts/show-hide-content.js", "javascripts/requestcorporationtaxnumberfrontend.js"))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    UglifyKeys.compressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages in Assets := Seq(concat,uglify),
    // only compress files generated by concat
    includeFilter in uglify := GlobFilter("requestcorporationtaxnumberfrontend-*.js")
  )
  .settings(majorVersion := 1)