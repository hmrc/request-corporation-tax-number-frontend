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

import models.cache.{CacheMap, DatedCacheMap}
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{IndexModel, IndexOptions, ReplaceOptions}
import play.api.Configuration
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SessionRepository @Inject() (config: Configuration, mongo: MongoComponent)(using ec: ExecutionContext)
    extends PlayMongoRepository[DatedCacheMap](
      mongoComponent = mongo,
      collectionName = config.get[String]("appName"),
      domainFormat = DatedCacheMap.formats,
      indexes = Seq(
        IndexModel(
          ascending("id"),
          IndexOptions()
            .unique(true)
            .name("idIndex")
        ),
        IndexModel(
          ascending(config.get[String]("mongodb.fieldName")),
          IndexOptions()
            .name(config.get[String]("mongodb.indexName"))
            .expireAfter(config.get[Long]("mongodb.timeToLiveInSeconds"), TimeUnit.SECONDS)
        )
      ),
      replaceIndexes = true
    ) {

  def upsert(cm: CacheMap): Future[Boolean] = {
    val cmUpdated = DatedCacheMap(cm.id, cm.data)
    val options   = ReplaceOptions().upsert(true)
    collection.replaceOne(equal("id", cm.id), cmUpdated, options).toFuture().map { result =>
      result.wasAcknowledged()
    }
  }

  def get(id: String): Future[Option[CacheMap]] =
    collection.find(equal("id", id)).headOption().map { datedCacheMap =>
      datedCacheMap.map { (value: DatedCacheMap) =>
        value.toCacheMap
      }
    }

}
