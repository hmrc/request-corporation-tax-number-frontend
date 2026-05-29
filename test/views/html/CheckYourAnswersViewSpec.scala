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

package views.html

import models.NormalMode
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}
import views.behaviours.ViewBehaviours
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBehaviours {

  val messageKeyPrefix: String = "checkYourAnswers"

  val view: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]

  val rowAsMessageKey: AnswerRow = AnswerRow(
    label = "checkYourAnswers.heading",
    answer = "site.submit",
    answerIsMessageKey = true,
    changeUrl = "/change-url"
  )

  val rowAsLiteral: AnswerRow = AnswerRow(
    label = "checkYourAnswers.heading",
    answer = "00000008",
    answerIsMessageKey = false,
    changeUrl = "/change-url"
  )

  val sectionWithoutHeading: AnswerSection = AnswerSection(None, Seq(rowAsMessageKey))

  val sectionWithHeading: AnswerSection =
    AnswerSection(Some("checkYourAnswers.heading"), Seq(rowAsMessageKey, rowAsLiteral))

  def createView: () => Html = () => view(sectionWithoutHeading)(fakeRequest, messages)

  "checkYourAnswers view" must {

    behave like normalPage(createView, messageKeyPrefix)
    behave like pageWithCorrectHeadingSize(createView)

    "render a govuk-summary-list" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, "dl.govuk-summary-list")
    }

    "render the dt with the govuk-!-width-one-half utility class" in {
      val doc = asDocument(createView())
      val dt  = doc.select("dl.govuk-summary-list dt.govuk-summary-list__key").first
      dt.hasClass("govuk-!-width-one-half") mustBe true
    }

    "render the change link with hidden accessibility text" in {
      val doc  = asDocument(createView())
      val link = doc.select("dl.govuk-summary-list dd.govuk-summary-list__actions a").first
      link.attr("href")                            mustBe "/change-url"
      link.text                                      must include(messages("site.edit"))
      link.select("span.govuk-visually-hidden").text must include(messages("checkYourAnswers.heading"))
    }

    "render the back link pointing to CompanyDetails NormalMode" in {
      val doc  = asDocument(createView())
      val back = doc.getElementById("back")
      back                must not be null
      back.attr("href") mustBe controllers.routes.CompanyDetailsController.onPageLoad(NormalMode).toString
    }

    "render the submit button with id submit and the site.submit text" in {
      val doc    = asDocument(createView())
      val button = doc.getElementById("submit")
      button        must not be null
      button.text mustBe messages("site.submit")
    }

    "render the report-technical-issue link" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, "a.hmrc-report-technical-issue")
    }

    "render the section heading when AnswerSection has a headingKey" in {
      val doc = asDocument(view(sectionWithHeading)(fakeRequest, messages))
      val h2  = doc.select("h2.govuk-heading-m")
      h2.size         must be >= 1
      h2.first.text mustBe messages("checkYourAnswers.heading")
    }

    "render answer text literally when answerIsMessageKey is false" in {
      val doc = asDocument(view(sectionWithHeading)(fakeRequest, messages))
      assertContainsText(doc, "00000008")
    }

    "render via render" in {
      val html = view.render(sectionWithoutHeading, fakeRequest, messages)
      html.toString must include("govuk-summary-list")
    }

    "render via f" in {
      val html = view.f(sectionWithoutHeading)(fakeRequest, messages)
      html.toString must include("govuk-summary-list")
    }

    "ref must return the view itself" in {
      view.ref mustBe view
    }
  }

}
