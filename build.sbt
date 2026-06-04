import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

ThisBuild / scalaVersion := "3.3.7"
ThisBuild / majorVersion := 1

lazy val microservice = Project("request-corporation-tax-number-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    RoutesKeys.routesImport ++= Seq("models._"),
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.govukfrontend.views.html.components.implicits._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._"
    ),
    libraryDependencies ++= AppDependencies(),
    PlayKeys.playDefaultPort := 9200
  )
  .settings(CodeCoverageSettings())
  .settings(
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:msg=unused import&src=conf/.*:s",
      "-Wconf:msg=unused pattern variable&src=conf/.*:s",
      "-Wconf:msg=unused import&src=routes/.*:s",
      "-Wconf:msg=unused import&src=html/.*:s",
      "-Wconf:msg=unused import&src=views/.*:s",
      "-Wconf:msg=unused pattern variable&src=routes/.*:s",
      "-Wconf:msg=unused private member&src=conf/.*:s",
      "-Wconf:msg=unused private member&src=routes/.*:s"
    )
  )

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
