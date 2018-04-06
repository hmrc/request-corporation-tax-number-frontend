/*
 * Copyright 2018 HM Revenue & Customs
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

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.CompanyDetails

class CompanyDetailsFormProvider @Inject() extends Mappings {

   def apply(): Form[CompanyDetails] = Form(
     mapping(
       "companyReferenceNumber" -> text("companyDetails.error.companyReferenceNumber.required")
         .verifying("companyDetails.error.companyReferenceNumber.regex",
           companyReferenceNumber => companyReferenceNumber.matches(
             "^([0-9]\\d{6,7}|\\d{6,7}|[a-zA-Z]{2}\\d{6})$")),
      "companyName" -> text("companyDetails.error.companyName.required")
        .verifying(maxLength(160, "companyDetails.error.companyName.length"))
    )(CompanyDetails.apply)(CompanyDetails.unapply)
   )
 }
