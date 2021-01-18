/*
 * Copyright 2021 HM Revenue & Customs
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

package utils

import controllers.routes
import models.CheckMode
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def companyDetailsReference: Option[AnswerRow] = userAnswers.companyDetails map {
    x => AnswerRow("companyDetailsReferenceNumber.checkYourAnswersLabel",
      s"${x.companyReferenceNumber}", answerIsMessageKey = false,
      routes.CompanyDetailsController.onPageLoad(CheckMode).url)
  }

  def companyDetailsName: Option[AnswerRow] = userAnswers.companyDetails map {
    x => AnswerRow("companyDetailsName.checkYourAnswersLabel",
      s"${x.companyName}", answerIsMessageKey = false,
      routes.CompanyDetailsController.onPageLoad(CheckMode).url)
  }
}
