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

package models

import play.api.libs.json._

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

case class CompanyNameAndDateOfCreation(companyName: String, dateOfCreation: Option[LocalDate])

object CompanyNameAndDateOfCreation {

  val companyNameAndDateOfCreationReads: Reads[CompanyNameAndDateOfCreation] = (json: JsValue) => {
    for {
      name <- (json \ "company_name").validate[String]
      dateOfCreation <- (json \ "date_of_creation").validate[LocalDate]
    } yield CompanyNameAndDateOfCreation(name, Some(dateOfCreation))
  }

  def parseJsonWithValidation(jsonString: String): Try[CompanyNameAndDateOfCreation] = {
    Json.parse(jsonString).validate(companyNameAndDateOfCreationReads) match {
      case JsSuccess(companyNameAndDateOfCreation, _) =>
        Success(companyNameAndDateOfCreation)
      case JsError(errors) =>
        Failure(new Exception("Error parsing CompanyNameAndDateOfCreation: " + errors.mkString))
    }
  }

}
