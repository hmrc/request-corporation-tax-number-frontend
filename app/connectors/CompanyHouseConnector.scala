/*
 * Copyright 2021 HM Revenue & Customs
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
import models.CompanyDetails
import play.api.{Configuration, Logger}
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import http.ProxyHttpClient

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CompanyHouseConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClient, proxyHttp: ProxyHttpClient) {

  val logger = Logger(classOf[CompanyHouseConnector])

  implicit val httpReads: HttpReads[HttpResponse] = new HttpReads[HttpResponse] {
    override def read(method: String, url: String, response: HttpResponse) = response
  }
  def getName(response: HttpResponse): String ={
    (response.json \ "company_name").as[String].toLowerCase.replace(" ","")
  }
  def validateCRN(data: CompanyDetails)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
    implicit val hc = HeaderCarrier().withExtraHeaders(("Authorization", appConfig.companyHouseRequestAuth))
    val httpClient =
      if (appConfig.proxyRequired) {
        proxyHttp
      } else {
        http
      }
    httpClient.GET(appConfig.companyHouseRequestUrl + data.companyReferenceNumber)

      .map { response =>
      response.status match {
        case OK =>
          logger.debug("[CompanyHouseConnector][validateCRN] " + "CRN found")
          Some(getName(response) == data.companyName.toLowerCase.replace(" ",""))
        case NOT_FOUND =>
          logger.debug("[CompanyHouseConnector][validateCRN] " + " CRN not found")
          Some(false)
        case TOO_MANY_REQUESTS =>
          logger.error("[CompanyHouseConnector][validateCRN] " + "request limit exceeded")
          None
        case _=>
          logger.error("[CompanyHouseConnector][validateCRN] " + response)
          None
      }
    }.recover{
      case e:Exception =>
        logger.error("[CompanyHouseConnector][validateCRN] submission to CompanyHouse failed: " + e)
        None
    }
  }
}
