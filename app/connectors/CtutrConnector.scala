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
import models.SubmissionResponse
import play.api.Logging
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CtutrConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClientV2) extends Logging {

  def processSubmission(submissionJson: JsValue)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[SubmissionResponse]] = {
    for {
      storeSubmissionResult: Option[String] <- storeSubmission(submissionJson)
      ctutrSubmissionResult: Option[SubmissionResponse] <- ctutrSubmission(storeSubmissionResult)
    } yield (ctutrSubmissionResult)
  }.recover {
    case e: Exception =>
      logger.warn(s"[CtutrConnector][ctutrSubmission] - processing submission failed - $e")
      None
  }

  def storeSubmission(submissionJson: JsValue)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[String]] = {
    val storeSubmissionUrl = s"${appConfig.ctutrUrl}/request-corporation-tax-number/store-submission"
    http
      .post(url"$storeSubmissionUrl")
      .withBody(submissionJson)
      .execute[HttpResponse]
      .map { response: HttpResponse =>
        response.status match {
          case CREATED =>
            logger.warn(s"[CtutrConnector][storeSubmission] - received 201 (CREATED) status from $storeSubmissionUrl")
            response.json.asOpt[String]
          case INTERNAL_SERVER_ERROR =>
            logger.warn(s"[CtutrConnector][storeSubmission] - received 500 status from $storeSubmissionUrl, while trying to store submission")
            None
        }

      }
  }

    def ctutrSubmission(storeSubmissionResult: Option[String])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[SubmissionResponse]] = {
      val submissionUrl = s"${appConfig.ctutrUrl}/request-corporation-tax-number/submission"
      storeSubmissionResult match {
        case Some(submissionId: String) => {
          http.post(url"$submissionUrl")
            .withBody(Json.toJson(submissionId))
            .execute[HttpResponse]
            .map {
              response: HttpResponse =>
                response.status match {
                  case OK =>
                    response.json.asOpt[SubmissionResponse]
                  case other =>
                    logger.warn(s"[CtutrConnector][ctutrSubmission] - received HTTP status $other from $submissionUrl")
                    None
                }
            }
        }
        case None =>
          logger.warn(s"[CtutrConnector][ctutrSubmission] - storeSubmission returned None for the submissionId not sending submission")
          Future.successful(None)
      }
    }
}
