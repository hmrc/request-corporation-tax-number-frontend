import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "request-corporation-tax-number-frontend"

scalaVersion := "2.13.13"

lazy val scoverageSettings =
  Seq(ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*models.*;.*repositories.*;" +
    ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*DataCacheConnector;",
    ScoverageKeys.coverageMinimumStmtTotal := 86,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    RoutesKeys.routesImport ++= Seq("models._"),
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.govukfrontend.views.html.components.implicits._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._"
    ),
    scalaSettings,
    defaultSettings(),
    scoverageSettings,
    libraryDependencies ++= AppDependencies.all,
    // To resolve dependency clash between flexmark v0.64.4+ and play-language to run accessibility tests, remove when versions align
    dependencyOverrides += "com.ibm.icu" % "icu4j" % "69.1",
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
    Assets / pipelineStages := Seq(concat,uglify),
    // only compress files generated by concat
    uglify / includeFilter := GlobFilter("requestcorporationtaxnumberfrontend-*.js")
  )
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(majorVersion := 1)
  .settings(
    scalacOptions -= "-Xmax-classfile-name",
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:cat=unused-imports&src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=html/.*:s",
      "-Wconf:cat=unused-imports&src=views/.*:s"
    )
  )

