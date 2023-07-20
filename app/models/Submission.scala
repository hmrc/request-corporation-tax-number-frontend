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

package models

import identifiers._
import play.api.libs.json.{Json, OFormat}
import utils.UserAnswers

case class Submission(companyDetails: CompanyDetails)

object Submission {

  implicit val format: OFormat[Submission] = Json.format[Submission]

  def apply(answers: UserAnswers): Submission = {

    require(answers.companyDetails.isDefined, "Company details was not answered")

    Submission(answers.companyDetails.get)
  }

  def asMap(e: Submission): Map[String, String] = {

    Map(
      CompanyDetailsId.toString -> e.companyDetails.toString
    )
  }
}
