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

package utils

import base.SpecBase
import org.scalatest.prop.TableDrivenPropertyChecks._
import utils.FormHelpers.toLowerCaseRemoveSpacesAndReplaceSmartChars

class FormHelpersSpec extends SpecBase {
  forAll(
    Table(
      ("userAnswerWithoutFormatting", "expectedResultAfterFormattingApplied"),
      // Just smartApostrophesOpen test cases
      ("test‘", "test'"),
      ("‘test", "'test"),
      ("‘t‘es‘t‘", "'t'es't'"),
      ("‘‘t‘es‘t‘‘", "''t'es't''"),
      // Just smartApostrophesClose test cases
      ("test’", "test'"),
      ("’test", "'test"),
      ("’t’es’t’", "'t'es't'"),
      ("’’t’es’t’’", "''t'es't''"),
      // smartApostrophesOpen and smartApostrophesClose cases
      ("’test‘", "'test'"),
      ("’‘test", "''test"),
      ("test’‘", "test''"),
      ("te’‘st", "te''st"),
      // Uppercase test cases
      ("TEST", "test"),
      ("Test", "test"),
      ("tesT", "test"),
      ("TesT", "test"),
      // WhiteSpace test cases
      (" test ", "test"),
      ("t e s t", "test"),
      ("te   st", "test"),
      ("     test    ", "test"),
      // Multiple formatting issues test cases
      ("  ’‘   T ‘e ’‘S t  ’‘  ", "''t'e''st''")
    )
  ){
    (userAnswerWithoutFormatting: String, expectedResultAfterFormattingApplied: String) =>
      s"Apply the correct formatting to: $userAnswerWithoutFormatting" in {
        toLowerCaseRemoveSpacesAndReplaceSmartChars(userAnswerWithoutFormatting) mustBe expectedResultAfterFormattingApplied
      }
  }

}
