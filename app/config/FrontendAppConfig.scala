/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.mvc.Call

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {
  private lazy val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "requestcorporationtaxnumberfrontend"

  lazy val analyticsToken: String = configuration.get[String](s"google-analytics.token")
  lazy val analyticsHost: String = configuration.get[String](s"google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  lazy val googleTagManagerId: String = configuration.get[String](s"google-tag-manager.id")

  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val feedbackSurveyUrl: String = configuration.get[String]("urls.feedback-survey")
  lazy val contactUsUrl: String = configuration.get[String]("urls.contactUs")

  lazy val ctutrUrl: Service = configuration.get[Service]("microservice.services.request-corporation-tax-number")

  lazy val languageTranslationEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val timeOutSeconds : Int = configuration.get[Int]("sessionTimeout.timeoutSeconds")
  lazy val timeOutCountDownSeconds: Int = configuration.get[Int]("sessionTimeout.time-out-countdown-seconds")
  lazy val refreshInterval: Int = timeOutSeconds + 10
  lazy val enableRefresh: Boolean= configuration.get[Boolean]("sessionTimeout.enableRefresh")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))
  def routeToSwitchLanguage: String => Call = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)
}
