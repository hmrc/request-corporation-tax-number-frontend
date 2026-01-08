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

package filters

import base.SpecBase
import org.apache.pekko.stream.Materializer
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class AntiMimeFilterSpec extends SpecBase {

  implicit val materializer: Materializer = app.materializer

  private val filter = new AntiMimeFilter(materializer)

  "AntiMimeFilter" must {

    "allow requests without mime type headers" in {
      val request = FakeRequest("GET", "/test")
      val nextFilter: RequestHeader => Future[Result] = _ => Future.successful(Results.Ok)

      val result = filter.apply(nextFilter)(request)

      status(result) mustEqual OK
      headers(result) mustEqual Map("X-Content-Type-Options" -> "nosniff")
    }

  }
}
