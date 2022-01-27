/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.FakeDataCacheConnector
import controllers.actions._
import forms.CompanyDetailsFormProvider
import models.{CompanyDetails, NormalMode}
import play.api.data.Form
import play.api.mvc.{Call, MessagesControllerComponents}
import play.api.test.Helpers._
import utils.FakeNavigator
import views.html.CompanyDetailsView

class CompanyDetailsControllerSpec extends ControllerSpecBase {

  implicit val cc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  def onwardRoute: Call = routes.IndexController.onPageLoad

  val formProvider: CompanyDetailsFormProvider = new CompanyDetailsFormProvider()
  val form: Form[CompanyDetails] = formProvider()
  val view: CompanyDetailsView = app.injector.instanceOf[CompanyDetailsView]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CompanyDetailsController(
      messagesApi,
      FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      formProvider,
      cc,
      view)

  def viewAsString(form: Form[_] = form): String = view(form, NormalMode)(fakeRequest, messages).toString

  "CompanyDetails Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val result = controller(someData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(form.fill(CompanyDetails("Big Company", "12345678")))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("companyName", "Big Company"), ("companyReferenceNumber", "12345678"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return OK and correct view for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("companyName", "Big Company"), ("companyReferenceNumber", "12345678"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
  }
}
