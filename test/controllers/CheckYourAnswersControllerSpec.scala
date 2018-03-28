/*
 * Copyright 2018 HM Revenue & Customs
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

import controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import models.{NormalMode, SubmissionFailed, SubmissionSuccessful}
import play.api.test.Helpers._
import services.SubmissionService
import uk.gov.hmrc.http.HeaderCarrier
import utils.UserAnswers

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
                 submissionService: SubmissionService = FakeSuccessfulSubmissionService) =
    new CheckYourAnswersController(frontendAppConfig, messagesApi, dataRetrievalAction, new DataRequiredActionImpl,
      submissionService)

  "Check Your Answers Controller" must {
    "return 200 for a GET" in {
      val result = controller(someData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "return 303 and correct view for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to Index for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("companyName", "Big Company"), ("companyReferenceNumber", "12345678"))
      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "Redirect to Confimration page on a POST when submission is successful" in {
      val result = controller(someData,FakeSuccessfulSubmissionService).onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ConfirmationController.onPageLoad().url)
    }

    "Redirect to Failed to submit on a POST when submission fails" in {
      val result = controller(someData, FakeFailingSubmissionService).onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.FailedToSubmitController.onPageLoad().url)
    }
  }
}

object FakeSuccessfulSubmissionService extends SubmissionService {
  override def ctutrSubmission(answers: UserAnswers)(implicit hc: HeaderCarrier) = Future.successful(SubmissionSuccessful)
}

object FakeFailingSubmissionService extends SubmissionService {
  override def ctutrSubmission(answers: UserAnswers)(implicit hc: HeaderCarrier) = Future.successful(SubmissionFailed)
}