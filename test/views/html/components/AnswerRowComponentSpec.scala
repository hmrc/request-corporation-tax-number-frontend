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

package views.html.components

import viewmodels.AnswerRow
import views.ViewSpecBase
import views.html.components.answer_row

class AnswerRowComponentSpec extends ViewSpecBase {

  val rowAsMessageKey: AnswerRow = AnswerRow(
    label = "checkYourAnswers.heading",
    answer = "site.submit",
    answerIsMessageKey = true,
    changeUrl = "/u"
  )

  val rowAsLiteral: AnswerRow = AnswerRow(
    label = "checkYourAnswers.heading",
    answer = "00000008",
    answerIsMessageKey = false,
    changeUrl = "/u"
  )

  "answer_row component" must {

    "render the answer via message key when answerIsMessageKey is true" in {
      val html = answer_row(rowAsMessageKey)(messages)
      html.toString must include(messages("site.submit"))
    }

    "render the answer literally when answerIsMessageKey is false" in {
      val html = answer_row(rowAsLiteral)(messages)
      html.toString must include("00000008")
    }

    "render via render" in {
      val html = answer_row.render(rowAsMessageKey, messages)
      html.toString must include("govuk-summary-list__row")
    }

    "render via f" in {
      val html = answer_row.f(rowAsMessageKey)(messages)
      html.toString must include("govuk-summary-list__row")
    }

    "expose itself via ref" in {
      answer_row.ref mustBe answer_row
    }
  }

}
