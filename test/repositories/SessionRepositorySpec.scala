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

package repositories

import base.SpecBase
import models.cache.{CacheMap, DatedCacheMap}
import play.api.Configuration
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

class SessionRepositorySpec extends SpecBase with DefaultPlayMongoRepositorySupport[DatedCacheMap] {

  private val config = Configuration.from(
    Map(
      "appName"                     -> this.databaseName,
      "mongodb.fieldName"           -> "lastUpdated",
      "mongodb.indexName"           -> "userAnswersExpiry",
      "mongodb.timeToLiveInSeconds" -> 3600L
    )
  )

  override protected val repository: SessionRepository =
    new SessionRepository(config, mongoComponent)

  private val cacheMap = CacheMap("test-id", Map("key" -> Json.toJson("value")))

  "SessionRepository" when {

    "upsert is called" must {

      "insert a new document and return true" in {
        repository.upsert(cacheMap).futureValue mustBe true
      }

      "update an existing document, replacing the data" in {
        val updated = CacheMap("test-id", Map("key" -> Json.toJson("updated-value")))
        repository.upsert(cacheMap).futureValue
        repository.upsert(updated).futureValue mustBe true
        repository.get("test-id").futureValue  mustBe Some(updated)
      }
    }

    "get is called" must {

      "return Some(CacheMap) for an existing document" in {
        repository.upsert(cacheMap).futureValue
        repository.get("test-id").futureValue mustBe Some(cacheMap)
      }

      "return None for a non-existent id" in {
        repository.get("non-existent-id").futureValue mustBe None
      }
    }
  }

}
