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

import base.SpecBase
import models.CompanyDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.TableDrivenPropertyChecks._
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.{MockUserAnswers, UserAnswers}

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

  "submission" must {

    val connector = new CompanyHouseConnector(frontendAppConfig, httpMock)

    "return true when the HTTP call to CoHo is a match" in {

      mockGetEndpoint(Future.successful(
        HttpResponse(200,
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

      val futureResult = connector.validateCRN(answers.companyDetails.get)

      whenReady(futureResult) { result =>
        result mustBe Some(false)
      }
    }

    "return false when the HTTP call has incorrect name/crn matching" in {
      mockGetEndpoint(Future.successful(
        HttpResponse(200,
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
            |   "company_name": "company name",
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

      val futureResult = connector.validateCRN(answers.companyDetails.get)

      whenReady(futureResult) { result =>
        result mustBe Some(true)
      }
    }

    "return false when the HTTP call returns a not found" in {
      mockGetEndpoint(Future.successful(
        HttpResponse(404,
          """
            |{
            |   "errors": [
            |      {
            |         "error": "company-profile-not-found",
            |         "type": "ch:service"
            |      }
            |   ]
            |}""".stripMargin)))

      val futureResult = connector.validateCRN(answers.companyDetails.get)

      whenReady(futureResult) { result =>
        result mustBe Some(false)
      }
    }

    "return None when a 429 gets returned" in {
      mockGetEndpoint(Future.successful(
        HttpResponse(429,
          """
            |{
            |   "errors": [
            |      {
            |         "error": "too many requests"
            |      }
            |   ]
            |}""".stripMargin)))

      val connector = new CompanyHouseConnector(frontendAppConfig, httpMock)
      val futureResult = connector.validateCRN(answers.companyDetails.get)

      whenReady(futureResult) { result =>
        result mustBe None
      }
    }

    forAll(
      Table(
        ("userAnswer", "mockCompanyName"),
        ("'''t'es't'''", "'''t'es't'''"),
        ("’’’t’es‘t‘‘‘", "'''t'es't'''"),
        ("’’’t’es‘t‘‘‘", "’’’t’es‘t‘‘‘"),
        ("'''t'es't'''", "’’’t’es‘t‘‘‘"),
      )
    ) {
      (userAnswer: String, mockCompanyName: String) =>
        s"return Some(true) given userAnswer: $userAnswer and companyName: $mockCompanyName" in {
          mockGetEndpoint(Future.successful(
            HttpResponse(200,
              s"""
                 |{
                 |   "company_name": "$mockCompanyName"
                 |}"""
                .stripMargin
            )
          ))

          val answers = MockUserAnswers.minimalValidUserAnswers(CompanyDetails("1234567", userAnswer))
          val futureResult = connector.validateCRN(answers.companyDetails.get)

          whenReady(futureResult) { result =>
            result mustBe Some(true)
          }
        }
    }
  }
}