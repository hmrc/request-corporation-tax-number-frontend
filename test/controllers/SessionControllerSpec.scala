/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.mvc.MessagesControllerComponents
import play.api.test.Helpers._
import views.html.SessionExpiredView

class SessionControllerSpec extends ControllerSpecBase {

  implicit val cc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  val view: SessionExpiredView = app.injector.instanceOf[SessionExpiredView]

  "SessionExpired Controller" must {
    "return 200 for a GET" in {
      val result = new SessionController(cc, view, messagesApi).onPageLoad()(fakeRequest)
      status(result) mustBe OK
    }

    "return the correct view for a GET" in {
      val result = new SessionController(cc, view, messagesApi).onPageLoad()(fakeRequest)
      contentAsString(result) mustBe view(routes.IndexController.onPageLoad())(fakeRequest, messages).toString
    }

    "keep a session alive" when {
      "keep alive action is called" in {
        val result = new SessionController(cc, view, messagesApi).keepAlive()(fakeRequest)
        status(result) mustBe OK
      }
    }
  }
}
