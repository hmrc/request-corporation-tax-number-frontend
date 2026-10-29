/*
 * Copyright 2026 HM Revenue & Customs
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
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, servicesConfig: ServicesConfig) {

  lazy val feedbackSurveyUrl: String =
    s"${configuration.get[String]("urls.feedback-survey")}?useServiceNavigation"

  lazy val companyHouseRegisterUrl: String  = configuration.get[String]("urls.company-house-register")
  lazy val getCompanyInformationUrl: String = configuration.get[String]("urls.get-information-about-a-company")
  lazy val contactUsUrl: String             = configuration.get[String]("urls.contactUs")

  lazy val companyHouseRequestUrl: String  = configuration.get[String]("companyHouse.requestUrl")
  lazy val companyHouseRequestAuth: String = configuration.get[String]("companyHouse.requestAuth")

  lazy val ctutrUrl: String = servicesConfig.baseUrl("request-corporation-tax-number")

  lazy val userZoomSurveyUrl: String = configuration.get[String]("urls.user-zoom-survey-url")

  lazy val timeOutSeconds: Int          = configuration.get[Int]("sessionTimeout.timeoutSeconds")
  lazy val timeOutCountDownSeconds: Int = configuration.get[Int]("sessionTimeout.time-out-countdown-seconds")
  lazy val enableRefresh: Boolean       = configuration.get[Boolean]("sessionTimeout.enableRefresh")

  lazy val showDelayBanner: Boolean = configuration.get[Boolean]("showDelayBanner")

  lazy val showZoomSurveyBanner: Boolean = configuration.get[Boolean]("showZoomSurveyBanner")

}
