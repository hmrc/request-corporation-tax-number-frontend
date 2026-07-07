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
import forms.CompanyDetailsFormProvider
import models.{CompanyDetails, NormalMode}
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours
import views.html.CompanyDetailsNoMatchView

class CompanyDetailsNoMatchViewSpec extends QuestionViewBehaviours[CompanyDetails] {

  val messageKeyPrefix: String = "companyDetailsNoMatch"

  override val form: Form[CompanyDetails]  = new CompanyDetailsFormProvider()()
  val view: CompanyDetailsNoMatchView      = app.injector.instanceOf[CompanyDetailsNoMatchView]
  def createView: () => Html               = () => view()(fakeRequest, messages)
  def createViewUsingForm: Form[?] => Html = (_: Form[?]) => view()(fakeRequest, messages)

  "CompanyDetailsNoMatch view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm
    )

    "render the try-again link pointing to CompanyDetails NormalMode" in {
      val doc  = asDocument(createView())
      val link = doc.getElementById("try-again-link")
      link                must not be null
      link.text         mustBe messages("companyDetailsNoMatch.button")
      link.attr("href") mustBe routes.CompanyDetailsController.onPageLoad(NormalMode).toString
    }

    "render the HmrcNewTabLink to getCompanyInformationUrl" in {
      val doc  = asDocument(createView())
      val link = doc.select("a[target=_blank]").first
      link                must not be null
      link.attr("href") mustBe frontendAppConfig.getCompanyInformationUrl
    }

    "render the first hint message" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages("companyDetailsNoMatch.hint.first"))
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
      val html = view.render(fakeRequest, messages)
      html.toString must include(messages("companyDetailsNoMatch.heading"))
    }

    "render via f" in {
      val html = view.f()(fakeRequest, messages)
      html.toString must include(messages("companyDetailsNoMatch.heading"))
    }

    "ref must return the view itself" in {
      view.ref mustBe view
    }
  }

}
