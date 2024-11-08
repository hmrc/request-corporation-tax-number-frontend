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

import config.FrontendAppConfig
import models.{CompanyDetails, CompanyNameAndDateOfCreation}
import play.api.Logging
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import utils.FormHelpers.toLowerCaseRemoveSpacesAndReplaceSmartChars

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try


class CompanyHouseConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClientV2) extends Logging {

  def validateCRNAndReturnCompanyDetails(data: CompanyDetails)
                                        (implicit ec: ExecutionContext)
  : Future[Option[(Boolean, Option[CompanyNameAndDateOfCreation])]] = {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    getCompanyDetails(data.companyReferenceNumber)
      .map { response =>
        response.status match {
          case OK =>
            logger.debug(s"[CompanyHouseConnector][validateCRN] CRN found")

            val companyDetailsWithDateOfCreation =
              Try(Json.parse(response.body).as[CompanyNameAndDateOfCreation]).toOption

            Some((getName(response) == toLowerCaseRemoveSpacesAndReplaceSmartChars(data.companyName), companyDetailsWithDateOfCreation))
          case NOT_FOUND =>
            logger.warn(s"[CompanyHouseConnector][validateCRN] CRN not found - $response")
            Some((false, None))
          case TOO_MANY_REQUESTS =>
            logger.error(s"[CompanyHouseConnector][validateCRN] request limit exceeded - $response")
            None
          case _ =>
            logger.error(s"[CompanyHouseConnector][validateCRN] Unexpected status: ${response.status} with body: ${response.body}")
            None
        }
      }.recover {
        case e: Exception =>
          logger.error("[CompanyHouseConnector][validateCRN] submission to CompanyHouse failed: " + e)
          None
      }
  }


  private def getName(response: HttpResponse): String =
    toLowerCaseRemoveSpacesAndReplaceSmartChars((response.json \ "company_name").as[String])

  private def getCompanyDetails(companyReferenceNumber: String)
                               (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[HttpResponse] = {
    val fullUrl = appConfig.companyHouseRequestUrl + companyReferenceNumber

    http
      .get(url"$fullUrl")
      .withProxy
      .setHeader("Authorization" -> appConfig.companyHouseRequestAuth)
      .execute[HttpResponse]
  }
}