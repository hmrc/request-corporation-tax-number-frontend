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
import views.behaviours.ViewBehaviours
import views.html.FailedToSubmitView

class FailedToSubmitViewSpec extends ViewBehaviours {

  val messageKeyPrefix: String = "failed_to_submit"
  val view: FailedToSubmitView = app.injector.instanceOf[FailedToSubmitView]

  def createView: () => Html = () => view()(fakeRequest, messages)

  "FailedToSubmit view" must {

    behave like normalPage(createView, messageKeyPrefix)
    behave like pageWithCorrectHeadingSize(createView)

    "render the guidance message" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages("failed_to_submit.guidance1"))
    }

    "not render a back link" in {
      val doc = asDocument(createView())
      assertNotRenderedById(doc, "back")
    }

    "render the report-technical-issue link" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, "a.hmrc-report-technical-issue")
    }

    "render via render" in {
      val html = view.render(fakeRequest, messages)
      html.toString must include(messages("failed_to_submit.heading"))
    }

    "render via f" in {
      val html = view.f()(fakeRequest, messages)
      html.toString must include(messages("failed_to_submit.heading"))
    }

    "ref must return the view itself" in {
      view.ref mustBe view
    }
  }

}
