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

package controllers.actions

import com.google.inject.{ImplementedBy, Inject}
import connectors.DataCacheConnector
import models.requests.OptionalDataRequest
import play.api.mvc.{ActionBuilder, ActionTransformer, AnyContent, BodyParsers, Request}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import utils.UserAnswers
import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject()(val dataCacheConnector: DataCacheConnector,
                                        override val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext) extends DataRetrievalAction {

  override protected def transform[A](request: Request[A]): Future[OptionalDataRequest[A]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    hc.sessionId match {
      case None => Future.failed(new IllegalStateException())
      case Some(sessionId) =>
        dataCacheConnector.fetch(sessionId.toString).map {
          case None => OptionalDataRequest(request, sessionId.toString, None)
          case Some(data) => OptionalDataRequest(request, sessionId.toString, Some(new UserAnswers(data)))
        }
    }
  }
}

@ImplementedBy(classOf[DataRetrievalActionImpl])
trait DataRetrievalAction extends ActionTransformer[Request, OptionalDataRequest] with ActionBuilder[OptionalDataRequest, AnyContent]
