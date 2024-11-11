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

import controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import models._
import org.mockito.Mockito.when
import play.api.mvc.{Call, MessagesControllerComponents}
import play.api.test.Helpers._
import services.SubmissionService
import uk.gov.hmrc.http.HeaderCarrier
import utils.UserAnswers
import views.html.{CheckYourAnswersView, CompanyDetailsNoMatchView}

import java.time.LocalDate
import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  object FakeSuccessfulSubmissionService extends SubmissionService {
    override def ctutrSubmission(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[SubmissionResult] =
      Future.successful(SubmissionSuccessful)
  }

  object FakeFailingSubmissionService extends SubmissionService {
    override def ctutrSubmission(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[SubmissionResult] =
      Future.successful(SubmissionFailed)
  }

  implicit val cc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  val checkYourAnswersView: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]
  val noMatchView: CompanyDetailsNoMatchView = app.injector.instanceOf[CompanyDetailsNoMatchView]

  def sessionExpired: Call = routes.CompanyDetailsController.onPageLoad(NormalMode)


  def testController(dataRetrievalAction: DataRetrievalAction = fakeDataRetrievalActionWithEmptyCacheMap,
                     submissionService: SubmissionService = FakeSuccessfulSubmissionService) =
    new CheckYourAnswersController(
      messagesApi,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      cc,
      companyHouseConnector,
      submissionService,
      checkYourAnswersView
    )

    ".onPageLoad" must {

      "return 200 for a GET" in {
        val result = testController(fakeDataRetrievalActionWithCacheMap).onPageLoad()(fakeRequest)

        status(result) mustBe OK
      }

      "return 303 and correct view for a GET if no existing data is found" in {
        val result = testController(fakeDataRetrievalActionWithUndefinedCacheMap).onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(sessionExpired.url)
      }
    }

    ".onSubmit" must {

      "redirect to session expired for a POST if no existing data is found" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("companyName", "Big Company"), ("companyReferenceNumber", "12345678"))
        val result = testController(fakeDataRetrievalActionWithUndefinedCacheMap).onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(sessionExpired.url)
      }

      "Redirect to Confirmation page on a POST when submission is successful" in {
        val result = testController(fakeDataRetrievalActionWithCacheMap, FakeSuccessfulSubmissionService).onSubmit()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ConfirmationController.onPageLoad().url)
      }

      "Redirect to Failed to submit on a POST when submission fails" in {
        val result = testController(fakeDataRetrievalActionWithCacheMap, FakeFailingSubmissionService).onSubmit()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.FailedToSubmitController.onPageLoad().url)
      }

      "Redirect to CompanyDetailsNoMatch on when details do not match" in {
        val numberOfDaysSinceCompanyCreated = 10

        val companyNameAndDateOfCreation = CompanyNameAndDateOfCreation("Company Name That Does Not Match",
          Some(LocalDate.now().minusDays(numberOfDaysSinceCompanyCreated)))

        when(companyHouseConnector.getCompanyDetails(companyDetails)) thenReturn Future(Right(companyNameAndDateOfCreation))
        val result = testController(fakeDataRetrievalActionWithCacheMap, FakeFailingSubmissionService).onSubmit()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CompanyDetailsNoMatchController.onPageLoad().url)
      }

      "Redirect to CompanyRegistered when company created less than or equal to seven days ago" in {
        val numberOfDaysSinceCompanyCreated = 7
        val companyNameAndDateOfCreation = CompanyNameAndDateOfCreation("Big Company", Some(LocalDate.now().minusDays(numberOfDaysSinceCompanyCreated)))
        when(companyHouseConnector.getCompanyDetails(companyDetails)).thenReturn(Future.successful(Right(companyNameAndDateOfCreation)))

        val result = testController(fakeDataRetrievalActionWithCacheMap).onSubmit()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CompanyRegisteredController.onPageLoad().url)
      }

      "Redirect to Confirmation when company created over seven days ago" in {
        val numberOfDaysSinceCompanyCreated = 8
        val companyNameAndDateOfCreation = CompanyNameAndDateOfCreation("Big Company", Some(LocalDate.now().minusDays(numberOfDaysSinceCompanyCreated)))
        when(companyHouseConnector.getCompanyDetails(companyDetails)).thenReturn(Future.successful(Right(companyNameAndDateOfCreation)))

        val result = testController(fakeDataRetrievalActionWithCacheMap).onSubmit()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ConfirmationController.onPageLoad().url)
      }

      "Redirect to Failed to submit when an exception is returned" in {
        when(companyHouseConnector.getCompanyDetails(companyDetails)).thenReturn(Future.successful(Left(CompaniesHouseJsonResponseParseError("some JS errors"))))

        val result = testController(fakeDataRetrievalActionWithCacheMap, FakeFailingSubmissionService).onSubmit()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.FailedToSubmitController.onPageLoad().url)
      }
    }
}
