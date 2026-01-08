/*
 * Copyright 2026 HM Revenue & Customs
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

import base.SpecBase
import connectors.DataCacheConnector
import models.requests.OptionalDataRequest
import models.cache.CacheMap
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.RecoverMethods
import org.scalatest.concurrent.ScalaFutures
import play.api.mvc.{BodyParsers, Request}
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with ScalaFutures with RecoverMethods {

  implicit val parser: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]

  class Harness(dataCacheConnector: DataCacheConnector) extends DataRetrievalActionImpl(dataCacheConnector, parser) {
    def callTransform[A](request: Request[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" when {

    "there is no data in the cache" must {
      "set userAnswers to 'None' in the request" in {
        val dataCacheConnector = mock(classOf[DataCacheConnector])
        when(dataCacheConnector.fetch(any())) thenReturn Future(None)
        val action = new Harness(dataCacheConnector)

        val futureResult = action.callTransform(fakeRequest.withSession(SessionKeys.sessionId -> "id"))

        whenReady(futureResult) { result =>
          result.userAnswers.isEmpty mustBe true
        }
      }
    }

    "there is data in the cache" must {
      "build a userAnswers object and add it to the request" in {
        val dataCacheConnector = mock(classOf[DataCacheConnector])
        when(dataCacheConnector.fetch(any())) thenReturn Future(Some(new CacheMap("id", Map())))
        val action = new Harness(dataCacheConnector)

        val futureResult = action.callTransform(fakeRequest.withSession(SessionKeys.sessionId -> "id"))

        whenReady(futureResult) { result =>
          result.userAnswers.isDefined mustBe true
        }
      }
    }

    "there is no session Id in the request" must {
      "throw an exception" in {
        val dataCacheConnector = mock(classOf[DataCacheConnector])
        val action = new Harness(dataCacheConnector)

        recoverToSucceededIf[IllegalStateException] {
          action.callTransform(fakeRequest)
        }
      }
    }
  }
}
