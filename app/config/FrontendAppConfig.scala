/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  private lazy val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "requestcorporationtaxnumberfrontend"

  lazy val analyticsToken = configuration.get[String](s"google-analytics.token")
  lazy val analyticsHost = configuration.get[String](s"google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"

  lazy val loginUrl = configuration.get[String]("urls.login")
  lazy val loginContinueUrl = configuration.get[String]("urls.loginContinue")
  lazy val feedbackSurveyUrl = configuration.get[String]("urls.feedback-survey")
  lazy val contactUsUrl = configuration.get[String]("urls.contactUs")

  lazy val ctutrUrl = configuration.get[Service]("request-corporation-tax-number")

  lazy val languageTranslationEnabled = configuration.get[Boolean]("microservice.services.features.welsh-translation")
  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))
  def routeToSwitchLanguage = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)
}
