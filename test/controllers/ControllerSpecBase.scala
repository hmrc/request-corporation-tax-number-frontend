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

package controllers

import base.SpecBase
import connectors.CompanyHouseConnector
import controllers.actions.FakeDataRetrievalAction
import identifiers.CompanyDetailsId
import models.{CompanyDetails, CompanyNameAndDateOfCreation}
import models.cache.CacheMap
import org.mockito.Mockito.{mock, when}
import play.api.libs.json.Json
import play.api.mvc.BodyParsers

import java.time.LocalDate
import scala.concurrent.Future

trait ControllerSpecBase extends SpecBase {

  val parser: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]
  val companyDetails: CompanyDetails = CompanyDetails("12345678", "Big Company")
  val companyNameAndDateOfCreation: CompanyNameAndDateOfCreation = CompanyNameAndDateOfCreation("Big Company", Some(LocalDate.now().minusDays(4)))

  val companyHouseConnector: CompanyHouseConnector = mock(classOf[CompanyHouseConnector])

  when(companyHouseConnector.getCompanyDetails(companyDetails))
    .thenReturn(Future.successful(Right(CompanyNameAndDateOfCreation("Big Company", None))))

  val cacheMapId = "id"

  def emptyCacheMap: CacheMap = CacheMap(cacheMapId, Map())

  def fakeDataRetrievalActionWithEmptyCacheMap = new FakeDataRetrievalAction(Some(emptyCacheMap), ec, parser)

  def fakeDataRetrievalActionWithUndefinedCacheMap = new FakeDataRetrievalAction(None, ec, parser)

  def fakeDataRetrievalActionWithCacheMap = new FakeDataRetrievalAction(
    Some(CacheMap(cacheMapId, Map(CompanyDetailsId.toString -> Json.toJson(companyDetails)))),
    ec,
    parser
  )
}
