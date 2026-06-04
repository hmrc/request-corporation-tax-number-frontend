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

import controllers.routes
import play.api.mvc.Call
import play.twirl.api.Html
import views.behaviours.ViewBehaviours
import views.html.SessionExpiredView

class SessionExpiredViewSpec extends ViewBehaviours {

  def call: Call               = routes.IndexController.onPageLoad()
  val view: SessionExpiredView = app.injector.instanceOf[SessionExpiredView]

  def createView: () => Html = () => view(call)(fakeRequest, messages)

  "Session Expired view" must {

    behave like normalPage(createView, "session_expired", "guidance")

    "render the continue button with id=continue and href to the start page" in {
      val doc    = asDocument(createView())
      val button = doc.getElementById("continue")
      button                must not be null
      button.text         mustBe messages("session_expired.button")
      button.attr("href") mustBe call.url
    }

    "render the guidance paragraph" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages("session_expired.guidance"))
    }

    "not render the timeout dialog meta tag (hasTimeOut = false)" in {
      val doc = asDocument(createView())
      doc.select("meta[name=hmrc-timeout-dialog]").isEmpty mustBe true
    }

    "render the report-technical-issue link" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, "a.hmrc-report-technical-issue")
    }

    "render via render" in {
      val html = view.render(call, fakeRequest, messages)
      html.toString must include(messages("session_expired.heading"))
    }

    "render via f" in {
      val html = view.f(call)(fakeRequest, messages)
      html.toString must include(messages("session_expired.heading"))
    }

    "ref must return the view itself" in {
      view.ref mustBe view
    }
  }

}
