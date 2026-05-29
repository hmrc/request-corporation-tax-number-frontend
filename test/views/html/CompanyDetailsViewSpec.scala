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
import play.api.data.{Form, FormError}
import play.twirl.api.Html
import views.behaviours.QuestionViewBehaviours
import views.html.CompanyDetailsView

import scala.jdk.CollectionConverters.*

class CompanyDetailsViewSpec extends QuestionViewBehaviours[CompanyDetails] {

  val messageKeyPrefix = "companyDetails"

  override val form: Form[CompanyDetails] = new CompanyDetailsFormProvider()()
  val view: CompanyDetailsView            = app.injector.instanceOf[CompanyDetailsView]

  def createView: () => Html = () => view(form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[?] => Html = (form: Form[?]) => view(form, NormalMode)(fakeRequest, messages)

  "CompanyDetails view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      routes.CompanyDetailsController.onSubmit(NormalMode).url,
      "companyName",
      "companyReferenceNumber"
    )

    behave like pageWithBackLink(createView)
    behave like pageWithCorrectHeadingSize(createView)

    "render both input labels with the govuk-!-font-weight-bold class" in {
      val doc      = asDocument(createView())
      val labels   = doc.select("label.govuk-label")
      val forAttrs = labels.eachAttr("for").asScala.toList
      forAttrs must contain allOf ("companyReferenceNumber", "companyName")
      labels.iterator().asScala.foreach { l =>
        l.hasClass("govuk-!-font-weight-bold") mustBe true
      }
    }

    "render hints for both input fields" in {
      val doc   = asDocument(createView())
      val hints = doc.select(".govuk-hint")
      hints.size must be >= 2
      assertContainsText(doc, messages("companyDetails.companyReferenceNumber.hint"))
      assertContainsText(doc, messages("companyDetails.companyName.hint"))
    }

    "render the HmrcNewTabLink pointing to companyHouseRegisterUrl" in {
      val doc  = asDocument(createView())
      val link = doc.select("a[target=_blank]").first
      link                must not be null
      link.attr("href") mustBe frontendAppConfig.companyHouseRegisterUrl
    }

    "render the submit button with id submit and the site.continue text" in {
      val doc    = asDocument(createView())
      val button = doc.getElementById("submit")
      button        must not be null
      button.text mustBe messages("site.continue")
    }

    "render the back link pointing to the Index page" in {
      val doc  = asDocument(createView())
      val back = doc.getElementById("back")
      back.attr("href") mustBe routes.IndexController.onPageLoad().toString
    }

    "render an error summary when the form has errors" in {
      val errored = form.withError(FormError("companyReferenceNumber", "error.required"))
      val doc     = asDocument(view(errored, NormalMode)(fakeRequest, messages))
      assertRenderedByClass(doc, "govuk-error-summary__title")
    }

    "render the report-technical-issue link" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, "a.hmrc-report-technical-issue")
    }

    "render via render" in {
      val html = view.render(form, NormalMode, fakeRequest, messages)
      html.toString must include(messages("companyDetails.heading"))
    }

    "render via f" in {
      val html = view.f(form, NormalMode)(fakeRequest, messages)
      html.toString must include(messages("companyDetails.heading"))
    }

    "ref must return the view itself" in {
      view.ref mustBe view
    }
  }

}
