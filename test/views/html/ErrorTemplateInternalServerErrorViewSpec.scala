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

import play.twirl.api.Html
import views.ViewSpecBase
import views.html.ErrorTemplateInternalServerErrorView

class ErrorTemplateInternalServerErrorViewSpec extends ViewSpecBase {

  val view: ErrorTemplateInternalServerErrorView = app.injector.instanceOf[ErrorTemplateInternalServerErrorView]

  def createView: () => Html = () =>
    view(
      pageTitle = messages("error.internalError.title"),
      heading = messages("error.internalError.heading"),
      message1 = messages("error.internalError.message1")
    )(fakeRequest, messages)

  "ErrorTemplateInternalServerError view" must {

    "render the page title in the <title> tag" in {
      val doc = asDocument(createView())
      doc.select("title").first.text must include(messages("error.internalError.title"))
    }

    "render the heading inside an h1 with the govuk-heading-l class" in {
      val doc = asDocument(createView())
      val h1  = doc.getElementsByTag("h1")
      h1.size                        mustBe 1
      h1.first.text                  mustBe messages("error.internalError.heading")
      h1.hasClass("govuk-heading-l") mustBe true
    }

    "render the guidance message" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages("error.internalError.message1"))
    }

    "render the report-technical-issue link" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, "a.hmrc-report-technical-issue")
    }

    "render via render" in {
      val html = view.render(
        messages("error.internalError.title"),
        messages("error.internalError.heading"),
        messages("error.internalError.message1"),
        fakeRequest,
        messages
      )
      html.toString must include(messages("error.internalError.heading"))
    }

    "render via f" in {
      val html = view.f(
        messages("error.internalError.title"),
        messages("error.internalError.heading"),
        messages("error.internalError.message1")
      )(fakeRequest, messages)
      html.toString must include(messages("error.internalError.heading"))
    }

    "ref must return the view itself" in {
      view.ref mustBe view
    }
  }

}
