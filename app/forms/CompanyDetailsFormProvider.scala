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

package forms

import javax.inject.Inject
import forms.mappings.Constraints
import play.api.data.Form
import play.api.data.Forms._
import models.CompanyDetails

class CompanyDetailsFormProvider @Inject() extends FormErrorHelper with Constraints {

  private val companyReferenceNumberRegex = "(?i)^([0-9]\\d{6,7}|\\d{6,7}|[A-Z]{2}\\d{6})$"
  private val companyReferenceNumberError = "companyDetails.error.companyReferenceNumber.regex"
  private val companyReferenceNumberLLPRegex = "(?i)^(?!OC|SL|SO|LP|NC|NL)([0-9]\\d{6,7}|\\d{6,7}|[A-Z]{2}\\d{6})*$"
  private val companyReferenceNumberLLPError = "companyDetails.error.companyReferenceNumber.llp.regex"
  private val companyReferenceNumberBlank = "companyDetails.error.companyReferenceNumber.required"

  private val companyNameMaxLength = 160
  private val companyNameLengthError = "companyDetails.error.companyName.length"
  private val companyNameBlank = "companyDetails.error.companyName.required"

  def apply(): Form[CompanyDetails] = Form(
    mapping(
      "companyReferenceNumber" -> text.verifying(
          firstError(nonEmpty(companyReferenceNumberBlank),
            regexValidation(companyReferenceNumberRegex, companyReferenceNumberError),
            regexValidation(companyReferenceNumberLLPRegex, companyReferenceNumberLLPError))),
      "companyName" -> text.verifying(
            firstError(nonEmpty(companyNameBlank),
            maxLength(companyNameMaxLength, companyNameLengthError)))
    )(CompanyDetails.apply)(CompanyDetails.unapply)
  )
 }
