/*
 * Copyright 2020 HM Revenue & Customs
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

package views

import controllers.routes
import models.NormalMode
import views.behaviours.ViewBehaviours
import views.html.IndexView

class IndexViewSpec extends ViewBehaviours {

  val view = app.injector.instanceOf[IndexView]

  def createView = () => view(call)(fakeRequest, messages)

  val call = routes.CompanyDetailsController.onPageLoad(NormalMode)

  "Index view" must {

    behave like normalPage(createView, "index")
  }

  "link should direct the user to check-your-answers page" in {
    val doc = asDocument(createView())
    doc.getElementById("start-now").attr("href") must include("/enter-company-details")
  }

  "Page should contain all to use this form content" in {
    val doc = asDocument(createView())
    assertContainsText(doc, messages("index.guidance"))
    assertContainsText(doc, messages("index.useForm.title"))
    assertContainsText(doc, messages("index.useForm.item1"))
    assertContainsText(doc, messages("index.useForm.item2"))
    assertContainsText(doc, messages("index.useForm.item3"))
  }

  "Page should contain before you start content" in {
    val doc = asDocument(createView())
    assertContainsText(doc, messages("index.beforeYouStart.title"))
    assertContainsText(doc, messages("index.beforeYouStart.item1"))
    assertContainsText(doc, messages("index.beforeYouStart.item2"))
  }
}
