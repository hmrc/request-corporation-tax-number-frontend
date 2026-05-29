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
import views.behaviours.*

class ErrorTemplateViewSpec extends ViewSpecBase {

  val view: ErrorTemplateView = app.injector.instanceOf[ErrorTemplateView]

  val pageTitle = "My title"
  val heading   = "My heading"
  val body      = "My message body"

  def createView: () => Html = () => view(pageTitle, heading, body)(fakeRequest, messages)

  "ErrorTemplate view" must {

    "render the page title" in {
      val doc = asDocument(createView())
      doc.select("title").first.text must include(pageTitle)
    }

    "render the heading inside an h1 with the govuk heading class" in {
      val doc = asDocument(createView())
      val h1  = doc.getElementsByTag("h1")
      h1.size                        mustBe 1
      h1.first.text                  mustBe heading
      h1.hasClass("govuk-heading-l") mustBe true
    }

    "render the message in the govuk-body paragraph" in {
      val doc = asDocument(createView())
      doc.select("p.govuk-body").text must include(body)
    }

    "render the service header banner" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages("service.name"))
    }

    "render the report-technical-issue link" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, "a.hmrc-report-technical-issue")
    }

    "render via render" in {
      val html = view.render(pageTitle, heading, body, fakeRequest, messages)
      html.toString must include(heading)
    }

    "render via f" in {
      val html = view.f(pageTitle, heading, body)(fakeRequest, messages)
      html.toString must include(heading)
    }

    "ref must return the view itself" in {
      view.ref mustBe view
    }
  }

}
