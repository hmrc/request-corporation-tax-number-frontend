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

package views.html.components

import views.ViewSpecBase
import views.html.components.heading

class HeadingComponentSpec extends ViewSpecBase {

  val headingKey = "checkYourAnswers.heading"

  "heading component" must {

    "render with the default headingSize via apply" in {
      val html = heading(headingKey)(messages)
      html.toString must include("govuk-heading-xl")
    }

    "render with a custom headingSize via apply" in {
      val html = heading(headingKey, "govuk-heading-l")(messages)
      html.toString must include("govuk-heading-l")
    }

    "render via render" in {
      val html = heading.render(headingKey, "govuk-heading-m", messages)
      html.toString must include("govuk-heading-m")
    }

    "render via f" in {
      val html = heading.f(headingKey, "govuk-heading-s")(messages)
      html.toString must include("govuk-heading-s")
    }

    "expose itself via ref" in {
      heading.ref mustBe heading
    }
  }

}
