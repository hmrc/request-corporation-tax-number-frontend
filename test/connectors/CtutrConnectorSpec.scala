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

package connectors

import models.{CompanyDetails, Submission, SubmissionResponse}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import scala.concurrent.duration.DurationInt
import scala.concurrent.Await
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.Injector

import scala.concurrent.ExecutionContext.Implicits.global


class CtutrConnectorSpec extends PlaySpec with WireMockHelper with ScalaFutures with ScalaCheckPropertyChecks with IntegrationPatience {

  def injector: Injector = app.injector

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val connector: CtutrConnector = app.injector.instanceOf[CtutrConnector]

  def mockPostEndpoint(url: String, requestBody: String, responseBody: String, expectedResponse: Int): StubMapping = {
    WireMock.stubFor(
      post(urlEqualTo(url))
        .withRequestBody(equalToJson(requestBody))
        .willReturn(
          aResponse()
            .withBody(responseBody)
            .withStatus(expectedResponse)
        )
    )
  }

  val submission: Submission = Submission(
    companyDetails = CompanyDetails(
      companyReferenceNumber = "1234",
      companyName = "company"
    )
  )

  val submissionResponse: SubmissionResponse = SubmissionResponse(
    id = "id",
    filename = "filename"
  )

  val objectId: JsValue = Json.parse(s"\"684ffb301ec3e3567ca327fb\"")

  "processSubmission" must {

    "return an Submission Response when the store-submission and submission calls succeed" in {

      mockPostEndpoint(
        url = "/request-corporation-tax-number/store-submission",
        requestBody = Json.stringify(Json.toJson(submission)),
        responseBody = Json.stringify(Json.toJson(Some(objectId))),
        expectedResponse = Status.CREATED
      )

      mockPostEndpoint(
        url = "/request-corporation-tax-number/submission",
        requestBody = Json.stringify(Json.toJson(objectId)),
        responseBody = Json.stringify(Json.toJson(Some(submissionResponse))),
        expectedResponse = Status.OK
      )

      val futureResult = connector.processSubmission(Json.toJson(submission))
      Await.result(futureResult, 5.seconds) mustBe Some(SubmissionResponse("id", "filename"))
    }

    "return None when the store-submission fails" in {

      mockPostEndpoint(
        url = "/request-corporation-tax-number/store-submission",
        requestBody = Json.stringify(Json.toJson(submission)),
        responseBody = Json.stringify(Json.toJson(None)),
        expectedResponse = Status.INTERNAL_SERVER_ERROR
      )

      val futureResult = connector.processSubmission(Json.toJson(submission))
      Await.result(futureResult, 5.seconds) mustBe None
    }

    "return nothing when the store-submission succeeds but submission fails" in {
      mockPostEndpoint(
        url = "/request-corporation-tax-number/store-submission",
        requestBody = Json.stringify(Json.toJson(submission)),
        responseBody = Json.stringify(Json.toJson(Some(objectId))),
        expectedResponse = Status.CREATED
      )

      mockPostEndpoint(
        url = "/request-corporation-tax-number/submission",
        requestBody = Json.stringify(Json.toJson(objectId)),
        responseBody = Json.stringify(Json.toJson("")),
        expectedResponse = Status.INTERNAL_SERVER_ERROR
      )

      val futureResult = connector.processSubmission(Json.toJson(submission))
      Await.result(futureResult, 5.seconds) mustBe None
    }

  }
}
