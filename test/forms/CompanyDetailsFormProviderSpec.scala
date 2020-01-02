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

package forms

import forms.behaviours.{FormBehaviours, StringFieldBehaviours}
import models.{MandatoryField, MaxLengthField, RegexField}
import play.api.data.Form

class CompanyDetailsFormProviderSpec extends StringFieldBehaviours with FormBehaviours {

  override val form: Form[_] = new CompanyDetailsFormProvider()()

  val validData: Map[String, String] = Map(
    "companyReferenceNumber" -> "AB123123",
    "companyName" -> "qwerty"
  )

  ".companyName" must {

    val fieldName = "companyName"
    val requiredKey = "companyDetails.error.companyName.required"
    val lengthKey = "companyDetails.error.companyName.length"
    val maxLength = 160

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(maxLength)
    )

    behave like formWithMaxLengthTextFields(
      MaxLengthField(
        fieldName = fieldName,
        errorMessageKey = lengthKey,
        maxLength = maxLength
      )
    )

    behave like formWithMandatoryTextFields(
      MandatoryField(
        fieldName = fieldName,
        errorMessageKey = requiredKey
      )
    )
  }

  ".companyReferenceNumber" must {

    val fieldName = "companyReferenceNumber"
    val requiredKey = "companyDetails.error.companyReferenceNumber.required"
    val invalidRegexKey = "companyDetails.error.companyReferenceNumber.regex"
    val invalidLLPRegexKey = "companyDetails.error.companyReferenceNumber.llp.regex"

    val maxLength = 8

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(maxLength)
    )

    behave like formWithRegex(
      RegexField(
        fieldName = fieldName,
        errorMessageKey = invalidRegexKey,
        invalidValue = "123"
      )
    )

    behave like formWithRegex(
      RegexField(
        fieldName = fieldName,
        errorMessageKey = invalidLLPRegexKey,
        invalidValue = "SL123123"
      )
    )

    behave like formWithRegex(
      RegexField(
        fieldName = fieldName,
        errorMessageKey = invalidLLPRegexKey,
        invalidValue = "OC123123"
      )
    )

    behave like formWithRegex(
      RegexField(
        fieldName = fieldName,
        errorMessageKey = invalidLLPRegexKey,
        invalidValue = "SO123123"
      )
    )

    behave like formWithRegex(
      RegexField(
        fieldName = fieldName,
        errorMessageKey = invalidLLPRegexKey,
        invalidValue = "LP123123"
      )
    )

    behave like formWithRegex(
      RegexField(
        fieldName = fieldName,
        errorMessageKey = invalidLLPRegexKey,
        invalidValue = "NC123123"
      )
    )

    behave like formWithRegex(
      RegexField(
        fieldName = fieldName,
        errorMessageKey = invalidLLPRegexKey,
        invalidValue = "NL123123"
      )
    )


    behave like formWithMandatoryTextFields(
      MandatoryField(
        fieldName = fieldName,
        errorMessageKey = requiredKey
      )
    )
  }
}
