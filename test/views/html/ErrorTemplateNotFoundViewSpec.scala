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
import views.html.ErrorTemplateNotFoundView

class ErrorTemplateNotFoundViewSpec extends ViewSpecBase {

  val view: ErrorTemplateNotFoundView = app.injector.instanceOf[ErrorTemplateNotFoundView]

  def createView: () => Html = () =>
    view(
      pageTitle = messages("error.pageNotFound.title"),
      heading = messages("error.pageNotFound.heading"),
      message1 = messages("error.pageNotFound.message1"),
      message2 = messages("error.pageNotFound.message2"),
      message3 = messages("error.pageNotFound.message3"),
      messageLink = messages("error.pageNotFound.messageLink")
    )(fakeRequest, messages)

  "ErrorTemplateNotFound view" must {

    "render the page title in the <title> tag" in {
      val doc = asDocument(createView())
      doc.select("title").first.text must include(messages("error.pageNotFound.title"))
    }

    "render the page heading as an h1 with the govuk-heading-l class" in {
      val doc = asDocument(createView())
      val h1  = doc.getElementsByTag("h1")
      h1.size                        mustBe 1
      h1.first.text                  mustBe messages("error.pageNotFound.heading")
      h1.hasClass("govuk-heading-l") mustBe true
    }

    "render all three guidance paragraphs" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages("error.pageNotFound.message1"))
      assertContainsText(doc, messages("error.pageNotFound.message2"))
      assertContainsText(doc, messages("error.pageNotFound.message3"))
    }

    "render the contact-us link pointing to the configured URL" in {
      val doc  = asDocument(createView())
      val link = doc.select("p.govuk-body a.govuk-link").last
      link.text         mustBe messages("error.pageNotFound.messageLink")
      link.attr("href") mustBe frontendAppConfig.contactUsUrl
    }

    "render via render" in {
      val html = view.render(
        messages("error.pageNotFound.title"),
        messages("error.pageNotFound.heading"),
        messages("error.pageNotFound.message1"),
        messages("error.pageNotFound.message2"),
        messages("error.pageNotFound.message3"),
        messages("error.pageNotFound.messageLink"),
        fakeRequest,
        messages
      )
      html.toString must include(messages("error.pageNotFound.heading"))
    }

    "render via f" in {
      val html = view.f(
        messages("error.pageNotFound.title"),
        messages("error.pageNotFound.heading"),
        messages("error.pageNotFound.message1"),
        messages("error.pageNotFound.message2"),
        messages("error.pageNotFound.message3"),
        messages("error.pageNotFound.messageLink")
      )(fakeRequest, messages)
      html.toString must include(messages("error.pageNotFound.heading"))
    }

    "ref must return the view itself" in {
      view.ref mustBe view
    }
  }

}
