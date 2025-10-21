/*
 * Copyright 2025 HM Revenue & Customs
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

package connectors

import base.SpecBase
import models.{Submission, SubmissionResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import utils.MockUserAnswers

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class CtutrConnectorSpec extends SpecBase with ScalaFutures {

  val mockRequestBuilderPost: RequestBuilder = mock(classOf[RequestBuilder])
  val mockHttpClient: HttpClientV2 = mock(classOf[HttpClientV2])

  when(mockRequestBuilderPost.withBody(any[JsValue])(any(), any(), any())).thenReturn(mockRequestBuilderPost)
  when(mockHttpClient.post(any())(any())).thenReturn(mockRequestBuilderPost)
  when(mockRequestBuilderPost.setHeader(any())).thenReturn(mockRequestBuilderPost)

  def mockPostEndpoint(expectedResponse: Future[HttpResponse]): OngoingStubbing[Future[HttpResponse]] =
    when(mockRequestBuilderPost.execute(any[HttpReads[HttpResponse]], any())).thenReturn(expectedResponse)

  "submission" must {

    implicit val hc: HeaderCarrier = HeaderCarrier()
    val answers = MockUserAnswers.minimalValidUserAnswers()
    val submission = Submission(answers)
    val connector = new CtutrConnector(frontendAppConfig, mockHttpClient)

    "return an Submission Response when the HTTP call succeeds" in {
      mockPostEndpoint(Future.successful(
        HttpResponse(200,
          """ |{
            |  "id": "id",
            |  "filename": "filename"
            |}""".stripMargin)))

      val futureResult = connector.ctutrSubmission(Json.toJson(submission))
      Await.result(futureResult, 5.seconds) mustBe Some(SubmissionResponse("id", "filename"))
    }

    "return nothing when the HTTP call fails" in {
      val enrolment = Submission(answers)
      mockPostEndpoint(Future.successful(HttpResponse(500, "")))

      val futureResult = connector.ctutrSubmission(Json.toJson(enrolment))
      Await.result(futureResult, 5.seconds) mustBe None
    }

  }
}
