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

package forms

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.FormError

class FormErrorHelperSpec extends AnyWordSpec with Matchers {

  object TestHelper extends FormErrorHelper

  "produceError" must {

    "return a Left containing the correct FormError" in {
      val result = TestHelper.produceError("fieldName", "error.key")
      result mustBe Left(Seq(FormError("fieldName", "error.key")))
    }
  }

}
