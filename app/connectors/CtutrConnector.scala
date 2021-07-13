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

import javax.inject.Inject

import config.FrontendAppConfig
import models.SubmissionResponse
import play.api.Logging
import play.api.libs.json.JsValue
import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw

import scala.concurrent.{ExecutionContext, Future}

class CtutrConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClient) extends Logging {

  def ctutrSubmission(submissionJson: JsValue)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[SubmissionResponse]] = {

    val submissionUrl = s"${appConfig.ctutrUrl}/request-corporation-tax-number/submission"

    http.POST(submissionUrl, submissionJson).map {
      response =>

        response.status match {
          case OK =>
            response.json.asOpt[SubmissionResponse]

          case other =>
            logger.warn(s"[CtutrConnector][ctutrSubmission] - received HTTP status $other from $submissionUrl")
            None
        }
    }.recover {
      case e: Exception =>
        logger.warn(s"[CtutrConnector][ctutrSubmission] - submission to $submissionUrl failed - $e")
        None
    }
  }
}
