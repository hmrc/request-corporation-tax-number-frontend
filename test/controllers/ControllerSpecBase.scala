/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import identifiers.CompanyDetailsId
import models.CompanyDetails
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext

trait ControllerSpecBase extends SpecBase {

  implicit val ec: ExecutionContext = injector.instanceOf[ExecutionContext]

  val cacheMapId = "id"

  def emptyCacheMap = CacheMap(cacheMapId, Map())

  def getEmptyCacheMap = new FakeDataRetrievalAction(Some(emptyCacheMap), ec)

  def dontGetAnyData = new FakeDataRetrievalAction(None, ec)

  def someData = new FakeDataRetrievalAction(
    Some(CacheMap(cacheMapId, Map(CompanyDetailsId.toString -> Json.toJson(CompanyDetails("Big Company", "12345678"))))), ec)
}
