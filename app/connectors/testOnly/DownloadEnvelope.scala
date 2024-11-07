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

import config.FrontendAppConfig
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

class DownloadEnvelope @Inject()(appConfig: FrontendAppConfig,
                                 http: HttpClientV2,
                                 cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends FrontendController(cc) with Logging {

  def downloadEnvelope(envelopeId: String): Action[AnyContent] =
    Action.async { implicit request: MessagesRequest[AnyContent] =>
      downloadEnvelopeRequest(envelopeId).map {
        case Right(source: Source[ByteString, _]) =>
          Ok.streamed(source, None)
            .withHeaders(
              CONTENT_TYPE        -> "application/zip",
              CONTENT_DISPOSITION -> s"""attachment; filename = "${envelopeId}.zip""""
            )
        case Left(error) => BadRequest(error)
      }
    }

  private def downloadEnvelopeRequest(envelopeId: String)(implicit hc: HeaderCarrier): Future[Either[String, Source[ByteString, _]]] =
    http
      .get(url"${appConfig.fileUploadUrl}/file-transfer/envelopes/${envelopeId}")
      .execute
      .map { response =>
        if (response.status == 200) Right(response.bodyAsSource) else Left(response.body)
      }
      .recover { case ex =>
        Left(s"Unknown problem when trying to download an envelopeId $envelopeId: " + ex.getMessage)
      }
}

