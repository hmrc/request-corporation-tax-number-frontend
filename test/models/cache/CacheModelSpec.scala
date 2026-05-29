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

package models.cache

import scala.language.implicitConversions
import base.SpecBase
import play.api.libs.json.{JsNumber, JsString, Json}

class CacheModelSpec extends SpecBase {

  "CacheMap.getEntry" must {

    "throw KeyStoreEntryValidationException when JSON cannot be read as the expected type" in {
      val cacheMap = CacheMap("id", Map("key" -> JsNumber(42)))
      val ex       = intercept[KeyStoreEntryValidationException] {
        cacheMap.getEntry[String]("key")
      }
      ex.getMessage must include("key")
    }
  }

  "KeyStoreEntryValidationException" must {

    "produce the correct message" in {
      val json = JsString("bad-value")
      val ex   = new KeyStoreEntryValidationException("myKey", json, classOf[Int], Seq.empty)
      val msg  = ex.getMessage
      msg must include("myKey")
      msg must include(Json.stringify(json))
      msg must include(classOf[Int].getName)
    }
  }

  "DatedCacheMap" must {

    "convert from CacheMap via given Conversion" in {
      val cm: CacheMap         = CacheMap("session-id", Map("k" -> JsString("v")))
      val dated: DatedCacheMap = cm
      dated.id   mustBe "session-id"
      dated.data mustBe cm.data
    }

    "convert back to CacheMap via toCacheMap" in {
      val dated  = DatedCacheMap("session-id", Map("k" -> JsString("v")))
      val result = dated.toCacheMap
      result mustBe CacheMap("session-id", Map("k" -> JsString("v")))
    }
  }

}
