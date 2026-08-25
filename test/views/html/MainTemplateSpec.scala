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

import play.twirl.api.{Html, HtmlFormat}
import views.ViewSpecBase
import views.html.MainTemplate

import scala.jdk.CollectionConverters.*

class MainTemplateSpec extends ViewSpecBase {

  val view: MainTemplate = app.injector.instanceOf[MainTemplate]

  val title       = "Test page"
  val mainContent = Html("<p id='main-content'>hello</p>")

  "MainTemplate" must {

    "render via apply with only the required arguments" in {
      val html = view(title)(mainContent)(fakeRequest, messages)
      html.toString must include("hello")
    }

    "render the page title in the <title> tag" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      doc.select("title").first.text must include(title)
    }

    "render the report-technical-issue link" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      assertRenderedByCssSelector(doc, "a.hmrc-report-technical-issue")
    }

    "render the service navigation component" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      assertRenderedByCssSelector(doc, ".govuk-service-navigation")
    }

    "request the service navigation component on every generated link to a shared PlatUI page" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))

      val sharedPagePaths = Seq(
        "/accessibility-statement/",
        "/contact/report-technical-problem",
        "/help/cookies",
        "/help/privacy",
        "/help/terms-and-conditions"
      )

      sharedPagePaths.foreach { path =>
        withClue(s"links to $path: ") {
          val hrefs = doc.select(s"""a[href*="$path"]""").eachAttr("href").asScala.toSeq
          hrefs must not be empty
          hrefs.foreach(_ must include("useServiceNavigation"))
        }
      }
    }

    "render the back link when backLink is Some" in {
      val doc  =
        asDocument(view(title, backLink = Some("/back-to-prev"))(mainContent)(fakeRequest, messages))
      val back = doc.getElementById("back")
      back                must not be null
      back.attr("href") mustBe "/back-to-prev"
    }

    "not render the back link when backLink is None" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      assertNotRenderedById(doc, "back")
    }

    "render the language toggle in the header (Welsh translation available)" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      val txt = doc.text()
      assert(txt.contains("ENG"))
      assert(txt.contains("CYM"))
    }

    "honour hasTimeOut = false (no timeout dialog meta tag)" in {
      val doc = asDocument(view(title, hasTimeOut = false)(mainContent)(fakeRequest, messages))
      doc.select("meta[name=hmrc-timeout-dialog]").isEmpty mustBe true
    }

    "render via render" in {
      val html: HtmlFormat.Appendable =
        view.render(title, true, None, mainContent, fakeRequest, messages)
      html.toString must include("hello")
    }

    "render via f" in {
      val html = view.f(title, true, None)(mainContent)(fakeRequest, messages)
      html.toString must include("hello")
    }

    "expose itself via ref" in {
      view.ref mustBe view
    }
  }

}
