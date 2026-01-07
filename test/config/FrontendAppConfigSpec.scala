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

import base.SpecBase
import play.api.i18n.Lang

class FrontendAppConfigSpec extends SpecBase {

  private val appConfig = app.injector.instanceOf[FrontendAppConfig]

  "FrontendAppConfig" must {

    "have the correct reportAProblemPartialUrl" in {
      appConfig.reportAProblemPartialUrl mustBe "http://localhost:9250/contact/problem_reports_ajax?service=requestcorporationtaxnumberfrontend"
    }

    "have the correct reportAProblemNonJSUrl" in {
      appConfig.reportAProblemNonJSUrl mustBe "http://localhost:9250/contact/problem_reports_nonjs?service=requestcorporationtaxnumberfrontend"
    }

    "have the correct betaFeedbackUrl" in {
      appConfig.betaFeedbackUrl mustBe "http://localhost:9250/contact/beta-feedback"
    }

    "have the correct loginUrl" in {
      appConfig.loginUrl mustBe "http://localhost:9000/request-corporation-tax-number-frontend"
    }

    "have the correct loginContinueUrl" in {
      appConfig.loginContinueUrl mustBe "http://localhost:9000/request-corporation-tax-number-frontend"
    }

    "have the correct feedbackSurveyUrl" in {
      appConfig.feedbackSurveyUrl mustBe "http://localhost:9514/feedback/CTUTR"
    }

    "have the correct companyHouseRegisterUrl" in {
      appConfig.companyHouseRegisterUrl mustBe "https://find-and-update.company-information.service.gov.uk"
    }

    "have the correct getCompanyInformationUrl" in {
      appConfig.getCompanyInformationUrl mustBe "https://www.gov.uk/get-information-about-a-company"
    }

    "have the correct contactUsUrl" in {
      appConfig.contactUsUrl mustBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/corporation-tax-enquiries"
    }

    "have the correct companyHouseRequestUrl" in {
      appConfig.companyHouseRequestUrl mustBe "http://localhost:9203/check-your-answers-stub/"
    }

    "have the correct companyHouseRequestAuth" in {
      appConfig.companyHouseRequestAuth mustBe ""
    }

    "have the correct ctutrUrl" in {
      appConfig.ctutrUrl mustBe "http://localhost:9201"
    }

    "have the correct languageTranslationEnabled" in {
      appConfig.languageTranslationEnabled mustBe true
    }

    "have the correct timeOutSeconds" in {
      appConfig.timeOutSeconds mustBe 900
    }

    "have the correct timeOutCountDownSeconds" in {
      appConfig.timeOutCountDownSeconds mustBe 120
    }

    "have the correct refreshInterval" in {
      appConfig.refreshInterval mustBe 910 // timeOutSeconds + 10
    }

    "have the correct enableRefresh" in {
      appConfig.enableRefresh mustBe true
    }

    "have the correct showDelayBanner" in {
      appConfig.showDelayBanner mustBe false
    }

    "have the correct languageMap" in {
      appConfig.languageMap mustBe Map(
        "english" -> Lang("en"),
        "cymraeg" -> Lang("cy")
      )
    }

    "return the correct route to switch language - EN" in {
      val enCall = appConfig.routeToSwitchLanguage("en")
      enCall.url mustBe "/ask-for-copy-of-your-corporation-tax-utr/language/en"
    }

    "return the correct route to switch language - CY" in {
      val cyCall = appConfig.routeToSwitchLanguage("cy")
      cyCall.url mustBe "/ask-for-copy-of-your-corporation-tax-utr/language/cy"
    }


  }
}
