/*
 * Copyright 2022 HM Revenue & Customs
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

package utils

import base.SpecBase
import controllers.routes
import identifiers._
import models._
import org.mockito.Mockito._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {
      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(mock(classOf[UserAnswers])) mustBe routes.IndexController.onPageLoad
      }

      "go to EmployerName from CapacityRegistering when personalBudgetHolderDirect is selected" in {
        val answers = mock(classOf[UserAnswers])
        when(answers.companyDetails) thenReturn Some(CompanyDetails("Big Company", "12345678"))
        navigator.nextPage(CompanyDetailsId, NormalMode)(answers) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }

    "in Check mode" must {
      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mock(classOf[UserAnswers])) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }
  }
}
