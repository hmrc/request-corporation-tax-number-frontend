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

package connectors

import base.SpecBase
import models.cache.CacheMap
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import play.api.libs.json.{JsString, Json}
import repositories.SessionRepository
import utils.CascadeUpsert

import scala.concurrent.Future

class DataCacheConnectorSpec extends SpecBase {

  val mockRepo: SessionRepository       = mock(classOf[SessionRepository])
  val cascadeUpsert: CascadeUpsert      = new CascadeUpsert
  val connector: DataCacheConnectorImpl = new DataCacheConnectorImpl(mockRepo, cascadeUpsert)

  val cacheId: String         = "test-id"
  val fieldKey: String        = "test-key"
  val emptyCacheMap: CacheMap = CacheMap(cacheId, Map())

  "save" when {

    "an existing cache map is found" must {
      "update it and return the updated map" in {
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(Some(emptyCacheMap)))
        when(mockRepo.upsert(any[CacheMap]())).thenReturn(Future.successful(true))

        val result = await(connector.save(cacheId, fieldKey, "value"))
        result.id   mustBe cacheId
        result.data mustBe Map(fieldKey -> JsString("value"))
      }
    }

    "no existing cache map is found" must {
      "create a new map and return it" in {
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(None))
        when(mockRepo.upsert(any[CacheMap]())).thenReturn(Future.successful(true))

        val result = await(connector.save(cacheId, fieldKey, "value"))
        result.id   mustBe cacheId
        result.data mustBe Map(fieldKey -> JsString("value"))
      }
    }
  }

  "remove" when {

    "an existing cache map is found" must {
      "remove the field and return true" in {
        val mapWithField = CacheMap(cacheId, Map(fieldKey -> JsString("value")))
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(Some(mapWithField)))
        when(mockRepo.upsert(any[CacheMap]())).thenReturn(Future.successful(true))

        val result = await(connector.remove(cacheId, fieldKey))
        result mustBe true
      }
    }

    "no existing cache map is found" must {
      "return false" in {
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(None))

        val result = await(connector.remove(cacheId, fieldKey))
        result mustBe false
      }
    }
  }

  "fetch" must {

    "return Some(CacheMap) when the cache map exists" in {
      when(mockRepo.get(cacheId)).thenReturn(Future.successful(Some(emptyCacheMap)))

      val result = await(connector.fetch(cacheId))
      result mustBe Some(emptyCacheMap)
    }

    "return None when no cache map exists" in {
      when(mockRepo.get(cacheId)).thenReturn(Future.successful(None))

      val result = await(connector.fetch(cacheId))
      result mustBe None
    }
  }

  "getEntry" must {

    "return Some(value) when the field exists in the cache" in {
      val mapWithField = CacheMap(cacheId, Map(fieldKey -> JsString("value")))
      when(mockRepo.get(cacheId)).thenReturn(Future.successful(Some(mapWithField)))

      val result = await(connector.getEntry[String](cacheId, fieldKey))
      result mustBe Some("value")
    }

    "return None when the field does not exist" in {
      when(mockRepo.get(cacheId)).thenReturn(Future.successful(Some(emptyCacheMap)))

      val result = await(connector.getEntry[String](cacheId, fieldKey))
      result mustBe None
    }
  }

  "addToCollection" when {

    "an existing collection is found" must {
      "append to it and return the updated map" in {
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(Some(emptyCacheMap)))
        when(mockRepo.upsert(any[CacheMap]())).thenReturn(Future.successful(true))

        val result = await(connector.addToCollection[String](cacheId, fieldKey, "item"))
        result.data mustBe Map(fieldKey -> Json.toJson(Seq("item")))
      }
    }

    "no existing cache map is found" must {
      "create a new collection with the value" in {
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(None))
        when(mockRepo.upsert(any[CacheMap]())).thenReturn(Future.successful(true))

        val result = await(connector.addToCollection[String](cacheId, fieldKey, "item"))
        result.data mustBe Map(fieldKey -> Json.toJson(Seq("item")))
      }
    }
  }

  "removeFromCollection" when {

    "the item is the only element" must {
      "remove the field entirely" in {
        val mapWithSeq = CacheMap(cacheId, Map(fieldKey -> Json.toJson(Seq("only-item"))))
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(Some(mapWithSeq)))
        when(mockRepo.upsert(any[CacheMap]())).thenReturn(Future.successful(true))

        val result = await(connector.removeFromCollection[String](cacheId, fieldKey, "only-item"))
        result.data.get(fieldKey) mustBe None
      }
    }

    "the item is one of many elements" must {
      "remove only that item" in {
        val mapWithSeq = CacheMap(cacheId, Map(fieldKey -> Json.toJson(Seq("keep", "remove-me"))))
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(Some(mapWithSeq)))
        when(mockRepo.upsert(any[CacheMap]())).thenReturn(Future.successful(true))

        val result = await(connector.removeFromCollection[String](cacheId, fieldKey, "remove-me"))
        result.data mustBe Map(fieldKey -> Json.toJson(Seq("keep")))
      }
    }

    "no cache map exists" must {
      "fail with an exception" in {
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(None))

        intercept[Exception] {
          await(connector.removeFromCollection[String](cacheId, fieldKey, "item"))
        }
      }
    }
  }

  "replaceInCollection" when {

    "a cache map exists" must {
      "replace the item at the given index" in {
        val mapWithSeq = CacheMap(cacheId, Map(fieldKey -> Json.toJson(Seq("first", "second"))))
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(Some(mapWithSeq)))
        when(mockRepo.upsert(any[CacheMap]())).thenReturn(Future.successful(true))

        val result = await(connector.replaceInCollection[String](cacheId, fieldKey, 1, "replaced"))
        result.data mustBe Map(fieldKey -> Json.toJson(Seq("first", "replaced")))
      }
    }

    "no cache map exists" must {
      "fail with an exception" in {
        when(mockRepo.get(cacheId)).thenReturn(Future.successful(None))

        intercept[Exception] {
          await(connector.replaceInCollection[String](cacheId, fieldKey, 0, "item"))
        }
      }
    }
  }

}
