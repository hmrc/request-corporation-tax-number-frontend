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

import viewmodels.{AnswerRow, AnswerSection}
import views.ViewSpecBase
import views.html.components.answer_section

class AnswerSectionComponentSpec extends ViewSpecBase {

  val row: AnswerRow =
    AnswerRow(label = "checkYourAnswers.heading", answer = "site.submit", answerIsMessageKey = true, changeUrl = "/u")

  val sectionWithoutHeading: AnswerSection = AnswerSection(None, Seq(row))
  val sectionWithHeading: AnswerSection    = AnswerSection(Some("checkYourAnswers.heading"), Seq(row))

  "answer_section component" must {

    "render the summary list without an h2 when headingKey is None" in {
      val html = answer_section(sectionWithoutHeading)(messages)
      val doc  = asDocument(html)
      assertRenderedByCssSelector(doc, "dl.govuk-summary-list")
      doc.select("h2.govuk-heading-m").isEmpty mustBe true
    }

    "render the section heading when headingKey is Some" in {
      val html = answer_section(sectionWithHeading)(messages)
      val doc  = asDocument(html)
      val h2   = doc.select("h2.govuk-heading-m")
      h2.size         must be >= 1
      h2.first.text mustBe messages("checkYourAnswers.heading")
    }

    "render via render" in {
      val html = answer_section.render(sectionWithoutHeading, messages)
      html.toString must include("govuk-summary-list")
    }

    "render via f" in {
      val html = answer_section.f(sectionWithoutHeading)(messages)
      html.toString must include("govuk-summary-list")
    }

    "expose itself via ref" in {
      answer_section.ref mustBe answer_section
    }
  }

}
