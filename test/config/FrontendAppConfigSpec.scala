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

class FrontendAppConfigSpec extends SpecBase {

  private val appConfig = app.injector.instanceOf[FrontendAppConfig]

  "FrontendAppConfig" must {

    "have the correct feedbackSurveyUrl" in {
      appConfig.feedbackSurveyUrl mustBe "http://localhost:9514/feedback/CTUTR?useServiceNavigation"
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

    "have the correct timeOutSeconds" in {
      appConfig.timeOutSeconds mustBe 900
    }

    "have the correct timeOutCountDownSeconds" in {
      appConfig.timeOutCountDownSeconds mustBe 120
    }

    "have the correct enableRefresh" in {
      appConfig.enableRefresh mustBe true
    }

    "have the correct showDelayBanner" in {
      appConfig.showDelayBanner mustBe false
    }

    "have the correct showZoomSurveyBanner" in {
      appConfig.showZoomSurveyBanner mustBe false
    }

  }

}
