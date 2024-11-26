/*
 * Copyright 2024 HM Revenue & Customs
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

import views.behaviours.ViewBehaviours
import views.html.CompanyRegisteredView

class CompanyRegisteredViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "companyRegistered"
  val view = app.injector.instanceOf[CompanyRegisteredView]
  val doc = asDocument(createView())

  def createView = () => view()(fakeRequest, messages)

  "company registered view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }


  "contain all expected text" in {
    val messages: Array[String] = Array(
      "companyRegistered.heading",
      "companyRegistered.line1",
      "companyRegistered.line2",
      "companyRegistered.heading2",
      "companyRegistered.line3Text",
      "companyRegistered.line3LinkText",
      "companyRegistered.line4",
      "companyRegistered.line5Bullet",
      "companyRegistered.line6Bullet",
      "companyRegistered.line7"
    )

    assertContainsMessages(doc, messages.toIndexedSeq: _*)
  }

  "have a link to Companies House register" in {
    val link = doc.getElementById("companies-house-link")
    link.text mustBe messages("companyRegistered.line3LinkText")
    link.attr("href") mustBe "https://find-and-update.company-information.service.gov.uk/"
  }
}
