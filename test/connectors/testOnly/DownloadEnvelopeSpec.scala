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

package connectors.testOnly

import base.SpecBase
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc._
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import org.apache.pekko.stream.Materializer
import play.api.http.Status
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers._

class DownloadEnvelopeSpec extends SpecBase with ScalaFutures {

  val fakeRequestDownloadEnvelope: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/test-only/download/envelopes/TestEnvelope")

  val mockHttpClientV2: HttpClientV2 = mock[HttpClientV2]
  val executeDownloadEnvelopeRequest: ExecuteDownloadEnvelopeRequest = mock[ExecuteDownloadEnvelopeRequest]

  implicit val cc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  implicit val hc: HeaderCarrier = new HeaderCarrier
  implicit val materializer: Materializer = app.materializer

  val downloadEnvelope: DownloadEnvelope = new DownloadEnvelope(
    frontendAppConfig,
    mockHttpClientV2,
    cc,
    executeDownloadEnvelopeRequest
  )

  "DownloadEnvelope" should {

    "return Ok and Download the envelope when executeGetRequest is called and returns Ok" in {

      when(executeDownloadEnvelopeRequest.executeGetRequest(any())(any(), any())).thenReturn(Future.successful(
        HttpResponse.apply(
          status = OK,
          bodyAsSource = Source.single(ByteString("test data")),
          headers = Map.empty
        ))
      )

      val result: Either[String, Source[ByteString, _]] = Await.result(downloadEnvelope.downloadEnvelopeRequest("TestEnvelope"), Duration.Inf)

      result mustBe a[Right[ByteString, _]]
      val actualByteString: ByteString = Await.result(result.toOption.get.runFold(ByteString.empty)(_ ++ _), Duration.Inf)
      actualByteString.utf8String mustBe "test data"
    }

    "return BadRequest executeGetRequest is called and returns BadRequest" in {

      when(executeDownloadEnvelopeRequest.executeGetRequest(any())(any(), any())).thenReturn(Future.successful(
        HttpResponse.apply(
          status = BAD_REQUEST,
          body = "file does not exist",
          headers = Map.empty
        ))
      )
      val result: Either[String, Source[ByteString, _]] = Await.result(downloadEnvelope.downloadEnvelopeRequest("TestEnvelope"), Duration.Inf)

      result.isLeft mustBe true
      assert(result.swap.exists(_.contains("file does not exist")))
    }

    "return Ok and Download the envelope when the envelop exists when downloadEnvelope is called" in {

      when(executeDownloadEnvelopeRequest.executeGetRequest(any())(any(), any())).thenReturn(Future.successful(
        HttpResponse.apply(
          status = OK,
          bodyAsSource = Source.single(ByteString("test data")),
          headers = Map.empty
        ))
      )

      val result: Future[Result] = Helpers.call(
        downloadEnvelope.downloadEnvelope("TestEnvelope"),
        fakeRequestDownloadEnvelope
      )

      status(result) mustBe Status.OK
      headers(result) mustBe Map(
        CONTENT_TYPE -> "application/zip",
        CONTENT_DISPOSITION -> s"""attachment; filename = "TestEnvelope.zip""""
      )
    }

    "return BadRequest with an error that indicates the envelop does not exists when downloadEnvelope is called" in {

      when(executeDownloadEnvelopeRequest.executeGetRequest(any())(any(), any())).thenReturn(Future.successful(
        HttpResponse.apply(
          status = BAD_REQUEST,
          body = "file does not exist",
          headers = Map.empty
        ))
      )

      val result: Future[Result] = Helpers.call(
        downloadEnvelope.downloadEnvelope("TestEnvelope"),
        fakeRequestDownloadEnvelope
      )

      status(result) mustBe Status.BAD_REQUEST
      contentAsString(result) must include("file does not exist")
    }
  }
}
