/*
 * Copyright 2024 HM Revenue & Customs
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
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK, TOO_MANY_REQUESTS}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HttpReads, HttpResponse, StringContextOps}
import utils.{MockUserAnswers, UserAnswers}

import java.net.URL
import java.time.LocalDate
import scala.concurrent.Future

class CompanyHouseConnectorSpec extends SpecBase with ScalaFutures {

  val httpMock: HttpClientV2 = mock(classOf[HttpClientV2])
  val mockRequestBuilderGet: RequestBuilder = mock(classOf[RequestBuilder])

  when(httpMock.get(any())(any())).thenReturn(mockRequestBuilderGet)
  when(mockRequestBuilderGet.setHeader(any())).thenReturn(mockRequestBuilderGet)
  when(mockRequestBuilderGet.withProxy).thenReturn(mockRequestBuilderGet)

  val answers: UserAnswers = MockUserAnswers.minimalValidUserAnswers()

  def mockGetEndpoint(expectedResponse: Future[HttpResponse]): OngoingStubbing[Future[HttpResponse]] =
    when(mockRequestBuilderGet.execute(any[HttpReads[HttpResponse]], any())).thenReturn(expectedResponse)

  ".getCompanyDetails" must {

    val connector = new CompanyHouseConnector(frontendAppConfig, httpMock)

    "return Right[CompanyNameAndDateOfCreation] with a defined date of creation, given JSON containing a company name and date of creation" in {

      mockGetEndpoint(Future.successful(
        HttpResponse(OK,
          """
            |{
            |   "links": {
            |      "filing_history": "/company/15428444/filing-history",
            |      "officers": "/company/15428444/officers",
            |      "persons_with_significant_control_statement": "/company/15428444/persons-with-significant-control-statement",
            |      "self": "/company/15428444"
            |   },
            |   "accounts": {
            |      "next_due": "2022-12-24",
            |      "next_accounts": {
            |         "period_start_on": "2021-03-24",
            |         "period_end_on": "2022-03-24",
            |         "due_on": "2022-12-24",
            |         "overdue": false
            |      },
            |      "next_made_up_to": "2022-03-24",
            |      "accounting_reference_date": {
            |         "day": "24",
            |         "month": "3"
            |      }
            |   },
            |   "company_number": "15428444",
            |   "date_of_creation": "2021-03-24",
            |   "type": "ltd",
            |   "undeliverable_registered_office_address": false,
            |   "company_name": "Company 15428444 LIMITED",
            |   "sic_codes": [
            |      "71200"
            |   ],
            |   "confirmation_statement": {
            |      "next_made_up_to": "2022-03-24",
            |      "overdue": false,
            |      "next_due": "2022-04-07"
            |   },
            |   "registered_office_is_in_dispute": false,
            |   "company_status": "active",
            |   "etag": "857b47e351ef2dad63f3734d6eac3440aa5aab28",
            |   "has_insolvency_history": false,
            |   "registered_office_address": {
            |      "address_line_1": "Companies House",
            |      "address_line_2": "Crownway",
            |      "country": "United Kingdom",
            |      "locality": "Cardiff",
            |      "postal_code": "CF14 3UZ"
            |   },
            |   "jurisdiction": "england-wales",
            |   "has_charges": false,
            |   "can_file": true
            |}""".stripMargin)))

      val result = connector.getCompanyDetails(answers.companyDetails.get).futureValue

      result mustBe Right(CompanyNameAndDateOfCreation("Company 15428444 LIMITED", Some(LocalDate.parse("2021-03-24"))))
    }

    "return Right[CompanyNameAndDateOfCreation] with am empty date of creation, given JSON containing just a company name" in {
      mockGetEndpoint(Future.successful(
        HttpResponse(OK, """{ "company_name": "Company 15428444 LIMITED"}""".stripMargin)
      ))

      val result = connector.getCompanyDetails(answers.companyDetails.get).futureValue
      result mustBe Right(CompanyNameAndDateOfCreation("Company 15428444 LIMITED", None))
    }

    "return Left[CompaniesHouseJsonResponseParseError] when the company name and date of creation are not in JSON response" in {
      mockGetEndpoint(Future.successful(HttpResponse(OK, "{}")))

      val result = connector.getCompanyDetails(answers.companyDetails.get).futureValue

      val expectedParseErrorMessage =
        "Error parsing CompanyNameAndDateOfCreation: " +
          "(,List(JsonValidationError(List('company_name' is undefined on object. Available keys are ''),List())))"

      result mustBe Left(CompaniesHouseJsonResponseParseError(expectedParseErrorMessage))
    }

    "return Left[CompaniesHouseResponseError] when the HTTP call returns a not found" in {
      mockGetEndpoint(Future.successful(
        HttpResponse(NOT_FOUND,
          """
            |{
            |   "errors": [
            |      {
            |         "error": "company-profile-not-found",
            |         "type": "ch:service"
            |      }
            |   ]
            |}""".stripMargin)))

      val result = connector.getCompanyDetails(answers.companyDetails.get).futureValue

      result mustBe Left(CompaniesHouseNotFoundResponse)
    }

    "return Left[CompaniesHouseResponseError] when a 429 gets returned" in {
      mockGetEndpoint(Future.successful(
        HttpResponse(TOO_MANY_REQUESTS,
          """
            |{
            |   "errors": [
            |      {
            |         "error": "too many requests"
            |      }
            |   ]
            |}""".stripMargin)))

      val result = connector.getCompanyDetails(answers.companyDetails.get).futureValue

      result mustBe Left(CompaniesHouseTooManyRequestsResponse)
    }

    "return Left[CompaniesHouseResponseError] when a 500 gets returned" in {
      mockGetEndpoint(Future.successful(
        HttpResponse(INTERNAL_SERVER_ERROR, """{ "some_error_key" : "some_error_value" }""".stripMargin)
      ))

      val result = connector.getCompanyDetails(answers.companyDetails.get).futureValue

      result mustBe Left(CompaniesHouseFailureResponse)
    }

    "return Left[CompaniesHouseResponseError] when .requestCompanyDetails throws " in {
      mockGetEndpoint(Future(
        throw new Exception("unreasonably obtuse exception from http call")
      ))

      val result = connector.getCompanyDetails(answers.companyDetails.get).futureValue

      result mustBe Left(CompaniesHouseExceptionError)
    }

    "call the http.get method exactly once per call" in {

      clearInvocations(httpMock)

      mockGetEndpoint(Future.successful(
        HttpResponse(OK, """{ "company_name": "Company 15428444 LIMITED"}""".stripMargin)
      ))

      connector.getCompanyDetails(answers.companyDetails.get).futureValue

      val expectedUrl: URL = url"http://localhost:9203/check-your-answers-stub/1234567"

      verify(httpMock, times(1)).get(expectedUrl)(connector.hc)
    }

  }
}
