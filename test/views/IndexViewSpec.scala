/*
 * Copyright 2023 HM Revenue & Customs
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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import views.behaviours.ViewBehaviours
import views.html.IndexView

class IndexViewSpec extends ViewBehaviours with GuiceOneAppPerSuite {

   def applicationBuilder: GuiceApplicationBuilder =
    GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .disable(classOf[com.kenshoo.play.metrics.PlayModule])

  val viewWithBanner: IndexView = applicationBuilder.configure("showDelayBanner" -> true).build().injector.instanceOf[IndexView]
  val viewWithoutBanner: IndexView = applicationBuilder.configure("showDelayBanner" -> false).build().injector.instanceOf[IndexView]
  val call: Call = routes.CompanyDetailsController.onPageLoad(NormalMode)

  "Index view" must {
    def createView = () => viewWithoutBanner(call)(fakeRequest, messages)
    behave like normalPage(createView, "index")
  }

  "link should direct the user to check-your-answers page" in {
    val doc = asDocument(viewWithoutBanner(call)(fakeRequest, messages))
    doc.getElementById("start-now").attr("href") must include("/enter-company-details")
  }

  "Page should contain all to use this form content" in {
    val doc = asDocument(viewWithoutBanner(call)(fakeRequest, messages))
    assertContainsText(doc, messages("index.guidance"))
    assertContainsText(doc, messages("index.useForm.title"))
    assertContainsText(doc, messages("index.useForm.item1"))
    assertContainsText(doc, messages("index.useForm.item2"))
    assertContainsText(doc, messages("index.useForm.item3"))
    assertContainsText(doc, messages("index.useForm.item4"))
  }

  "Page should contain 'you will need' content" in {
    val doc = asDocument(viewWithoutBanner(call)(fakeRequest, messages))
    assertContainsText(doc, messages("index.youWillNeed.title"))
    assertContainsText(doc, messages("index.youWillNeed.item1"))
    assertContainsText(doc, messages("index.youWillNeed.item2"))
  }

  "Page should contain before you start content" in {
    val doc = asDocument(viewWithoutBanner(call)(fakeRequest, messages))
    assertContainsText(doc, messages("index.beforeYouStart.title"))
    assertContainsText(doc, messages("index.beforeYouStart.p"))
    assertContainsText(doc, messages("index.beforeYouStart.item1"))
    assertContainsText(doc, messages("index.beforeYouStart.item2"))
  }

  "Page should contain service delay banner content" in {
    val doc = asDocument(viewWithBanner(call)(fakeRequest, messages))
    assertContainsText(doc, messages("index.serviceDelay.title"))
    assertContainsText(doc, messages("index.serviceDelay.heading"))
    assertContainsText(doc, messages("index.serviceDelay.message"))
  }

  "Page should not contain service delay banner" in {
    val doc = asDocument(viewWithoutBanner(call)(fakeRequest, messages))
    assertNotRenderedByClass(doc, "govuk-notification-banner")
  }


}
