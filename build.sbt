import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "request-corporation-tax-number-frontend"

lazy val scoverageSettings =
  Seq(ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*models.*;.*repositories.*;" +
    ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*DataCacheConnector;" +
    ".*ControllerConfiguration;.*LanguageSwitchController",
    ScoverageKeys.coverageMinimum := 85,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    RoutesKeys.routesImport ++= Seq("models._"),
    TwirlKeys.templateImports ++= Seq(
      "views.html.GovukWrapper",
      "uk.gov.hmrc.govukfrontend.views.html.components.implicits._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.govukfrontend.views.html.helpers._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._"
    ),
    scalaSettings,
    publishingSettings,
    defaultSettings(),
    scoverageSettings,
    scalacOptions ++= Seq("-feature"),
    libraryDependencies ++= AppDependencies.all,
    dependencyOverrides += "commons-codec" % "commons-codec" % "1.12",
    retrieveManaged := true,
    PlayKeys.playDefaultPort := 9200,
    // concatenate js
    Concat.groups := Seq(
      "javascripts/requestcorporationtaxnumberfrontend-app.js" -> group(Seq("javascripts/show-hide-content.js", "javascripts/requestcorporationtaxnumberfrontend.js", "javascripts/timeoutDialog.js"))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages in Assets := Seq(concat,uglify),
    // only compress files generated by concat
    includeFilter in uglify := GlobFilter("requestcorporationtaxnumberfrontend-*.js")
  )
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(majorVersion := 1)
  scalaVersion := "2.12.14"

// Silence unused import in views and routes
val silencerVersion = "1.7.5"
libraryDependencies ++= Seq(
  compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
  "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
)

scalacOptions ++= Seq(
  "-P:silencer:pathFilters=views;routes"
)
