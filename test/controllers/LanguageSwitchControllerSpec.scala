/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers

import config.FrontendAppConfig
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.mvc.{Cookie, Cookies, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.language.LanguageUtils

class LanguageSwitchControllerSpec extends ControllerSpecBase with GuiceOneAppPerSuite {

  implicit val cc: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]

  val langUtils: LanguageUtils = app.injector.instanceOf[LanguageUtils]
  val config: Configuration = app.injector.instanceOf[Configuration]
  val appConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  object TestLanguageSwitchController extends LanguageSwitchController(config, appConfig, langUtils, cc, messagesApi)

  def testLanguageSelection(language: String, expectedCookieValue: String): Unit = {
    val request = FakeRequest()
    val result = TestLanguageSwitchController.switchToLanguage(language)(request)
    val resultCookies: Cookies = cookies(result)
    resultCookies.size mustBe 1
    val cookie: Cookie = resultCookies.head
    cookie.name mustBe "PLAY_LANG"
    cookie.value mustBe expectedCookieValue
  }

  "Hitting language selection endpoint" must {

    "redirect to English translated start page if English language is selected" in {
      testLanguageSelection("english", "en")
    }

    "redirect to Welsh translated start page if Welsh language is selected" in {
      testLanguageSelection("cymraeg", "cy")
    }
  }
}
