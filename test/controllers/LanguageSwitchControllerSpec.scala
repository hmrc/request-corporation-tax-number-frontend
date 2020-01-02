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

package controllers

import config.FrontendAppConfig
import org.scalatestplus.play.OneAppPerSuite
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.play.test.UnitSpec

class LanguageSwitchControllerSpec extends UnitSpec with OneAppPerSuite {

  implicit val cc: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]

  val messagesApi: MessagesApi = app.injector.instanceOf(classOf[MessagesApi])
  val langUtils: LanguageUtils = app.injector.instanceOf[LanguageUtils]
  val config: Configuration = app.injector.instanceOf[Configuration]
  val appConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  object TestLanguageSwitchController extends LanguageSwitchController(config, appConfig, langUtils, cc, messagesApi)

  "Hitting language selection endpoint" must {

    "redirect to English translated start page if English language is selected" ignore {
      val request = FakeRequest()
      val result = TestLanguageSwitchController.switchToLanguage("english")(request)
      header("Set-Cookie", result) shouldBe Some("PLAY_LANG=en; Path=/;;PLAY_FLASH=switching-language=true; Path=/; HTTPOnly")
    }

    "redirect to Welsh translated start page if Welsh language is selected" ignore {
      val request = FakeRequest()
      val result = TestLanguageSwitchController.switchToLanguage("cymraeg")(request)
      header("Set-Cookie", result) shouldBe Some("PLAY_LANG=cy; Path=/;;PLAY_FLASH=switching-language=true; Path=/; HTTPOnly")
    }

  }
}
