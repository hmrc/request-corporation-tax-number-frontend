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
import forms.CompanyDetailsFormProvider
import models.{CompanyDetails, NormalMode}
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours
import views.html.CompanyDetailsView

class CompanyDetailsViewSpec extends QuestionViewBehaviours[CompanyDetails] {

  val messageKeyPrefix = "companyDetails"

  override val form = new CompanyDetailsFormProvider()()
  val view = app.injector.instanceOf[CompanyDetailsView]

  def createView = () => view(form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => view(form, NormalMode)(fakeRequest, messages)


  "CompanyDetails view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.CompanyDetailsController.onSubmit(NormalMode).url, "companyName", "companyReferenceNumber")
  }
}
