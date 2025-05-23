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

import controllers.actions._
import play.api.mvc.MessagesControllerComponents
import play.api.test.Helpers._
import views.html.ConfirmationView

class ConfirmationControllerSpec extends ControllerSpecBase {

  implicit val cc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  val view: ConfirmationView = app.injector.instanceOf[ConfirmationView]

  def controller(dataRetrievalAction: DataRetrievalAction = fakeDataRetrievalActionWithEmptyCacheMap) = new ConfirmationController(
    messagesApi,
    dataRetrievalAction,
    cc,
    new DataRequiredActionImpl,
    view)

  def viewAsString(): String = view()(fakeRequest, messages).toString

  "Confirmation Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
