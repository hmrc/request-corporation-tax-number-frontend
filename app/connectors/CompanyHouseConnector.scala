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

import config.FrontendAppConfig
import models._
import play.api.Logging
import play.api.http.Status.{OK, NOT_FOUND, TOO_MANY_REQUESTS}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}


class CompanyHouseConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClientV2) extends Logging {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  def getCompanyDetails(data: CompanyDetails)(implicit ec: ExecutionContext)
  : Future[Either[CompaniesHouseConnectorFailure, CompanyNameAndDateOfCreation]] = {

    requestCompanyDetails(data.companyReferenceNumber).map { response =>
      response.status match {
        case OK =>
          logger.debug(s"[CompanyHouseConnector][validateCRN] CRN found")

          CompanyNameAndDateOfCreation.parseJsonWithValidation(response.body) match {
            case Success(companyNameAndDateOfCreation) =>
              Right(companyNameAndDateOfCreation)

            case Failure(companyNameAndDateOfCreationJsParseException) =>
              // some companies do not have a date of creation, try to retrieve the just the company name from the response
              Try((response.json \ "company_name").as[String]) match {
                case Success(companyName: String) =>
                  Right(CompanyNameAndDateOfCreation(companyName, dateOfCreation = None))

                case Failure(_) =>
                  logger.warn(
                    s"[CompanyHouseConnector][requestCompanyDetails] Error parsing JSON - " +
                      s"response: $response, parseErrors: $companyNameAndDateOfCreationJsParseException"
                  )

                  Left(CompaniesHouseJsonResponseParseError(companyNameAndDateOfCreationJsParseException.getMessage))
              }
          }
        case NOT_FOUND =>
          logger.warn(s"[CompanyHouseConnector][requestCompanyDetails] CRN not found - $response")
          Left(CompaniesHouseNotFoundResponse)
        case TOO_MANY_REQUESTS =>
          logger.error(s"[CompanyHouseConnector][requestCompanyDetails] request limit exceeded - $response")
          Left(CompaniesHouseTooManyRequestsResponse)
        case _ =>
          logger.error(s"[CompanyHouseConnector][requestCompanyDetails] Unexpected status: ${response.status} with body: ${response.body}")
          Left(CompaniesHouseFailureResponse)
      }
    }.recover {
      case e: Exception =>
        logger.error("[CompanyHouseConnector][requestCompanyDetails] submission to CompanyHouse failed: " + e)
        Left(CompaniesHouseExceptionError)
    }
  }


  private def requestCompanyDetails(companyReferenceNumber: String)
                                   (implicit ec: ExecutionContext): Future[HttpResponse] = {

    val fullUrl = appConfig.companyHouseRequestUrl + companyReferenceNumber

    http
      .get(url"$fullUrl")
      .withProxy
      .setHeader("Authorization" -> appConfig.companyHouseRequestAuth)
      .execute[HttpResponse]
  }
}
