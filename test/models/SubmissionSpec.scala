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

package models

import base.SpecBase
import identifiers.CompanyDetailsId
import utils.MockUserAnswers

class SubmissionSpec extends SpecBase {

  "Submission.apply" must {

    "create a Submission when company details are present" in {
      val answers    = MockUserAnswers.minimalValidUserAnswers()
      val submission = Submission(answers)
      submission.companyDetails mustBe answers.companyDetails.get
    }

    "throw an IllegalArgumentException when company details are not answered" in {
      val answers = MockUserAnswers.nothingAnswered
      intercept[IllegalArgumentException] {
        Submission(answers)
      }
    }
  }

  "Submission.asMap" must {

    "return a map with the company details string" in {
      val answers    = MockUserAnswers.minimalValidUserAnswers()
      val submission = Submission(answers)
      val result     = Submission.asMap(submission)
      result mustBe Map(CompanyDetailsId.toString -> submission.companyDetails.toString)
    }
  }

}
