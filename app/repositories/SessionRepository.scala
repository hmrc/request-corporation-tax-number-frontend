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

package repositories

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{IndexModel, IndexOptions, ReplaceOptions}
import play.api.Configuration
import play.api.libs.json.{Format, JsValue, Json, OFormat}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.{MongoFormats, MongoJavatimeFormats}

import java.time.{LocalDateTime, ZoneId}
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

case class DatedCacheMap(id: String,
                         data: Map[String, JsValue],
                         lastUpdated: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))) extends MongoFormats {

  def toCacheMap: CacheMap = {
    CacheMap(this.id, this.data)
  }
}

object DatedCacheMap {
  implicit def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)

  implicit val dateFormat: Format[LocalDateTime] = MongoJavatimeFormats.localDateTimeFormat
  implicit val formats: OFormat[DatedCacheMap] = Json.format[DatedCacheMap]
}

class MongoRepository(config: Configuration, mongo: MongoComponent)(implicit ec: ExecutionContext)
  extends PlayMongoRepository[DatedCacheMap](
    mongoComponent = mongo,
    collectionName = config.get[String]("appName"),
    domainFormat = DatedCacheMap.formats,
    indexes = Seq(IndexModel(
      ascending(config.get[String]("mongodb.fieldName")),
      IndexOptions()
        .name(config.get[String]("mongodb.indexName"))
        .expireAfter(config.get[Int]("mongodb.timeToLiveInSeconds"), TimeUnit.SECONDS)
    )),
    replaceIndexes = false) {

  def upsert(cm: CacheMap): Future[Boolean] = {
    val cmUpdated = DatedCacheMap(cm.id, cm.data)
    val options = ReplaceOptions().upsert(true)
    collection.replaceOne(equal("id", cm.id), cmUpdated, options).toFuture().map { result =>
      result.wasAcknowledged()
    }
  }

  def get(id: String): Future[Option[CacheMap]] = {
    collection.find(equal("id", id)).headOption().map { datedCacheMap =>
      datedCacheMap.map { value: DatedCacheMap =>
        value.toCacheMap
      }
    }
  }
}

@Singleton
class SessionRepository @Inject()(config: Configuration, mongoComponent: MongoComponent)(implicit ec: ExecutionContext) {

  private lazy val sessionRepository = new MongoRepository(config, mongoComponent)

  def apply(): MongoRepository = sessionRepository
}
