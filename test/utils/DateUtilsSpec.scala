/*
 * Copyright 2025 HM Revenue & Customs
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
import models.CompanyNameAndDateOfCreation

import java.time.{LocalDate, ZoneId}


class DateUtilsSpec extends SpecBase {

  val now: LocalDate = LocalDate.now(ZoneId.of("GMT"))
  val testCompanyName = "test"

  "DateUtils.isCompanyRecentlyCreated" must {
    "return true for a very recently created company" in {
      val companyNameAndDateOfCreation = CompanyNameAndDateOfCreation(testCompanyName, Some(now))

      DateUtils.isCompanyRecentlyCreated(companyNameAndDateOfCreation) mustBe true
    }

    "return true for a company created 7 days ago" in {
      val companyNameAndDateOfCreation = CompanyNameAndDateOfCreation(testCompanyName, Some(now.minusDays(7)))

      DateUtils.isCompanyRecentlyCreated(companyNameAndDateOfCreation) mustBe true
    }

    "return false for a company created 8 days ago" in {
      val companyNameAndDateOfCreation = CompanyNameAndDateOfCreation(testCompanyName, Some(now.minusDays(8)))

      DateUtils.isCompanyRecentlyCreated(companyNameAndDateOfCreation) mustBe false
    }

    "return false for a company created over two months ago" in {
      val companyNameAndDateOfCreation = CompanyNameAndDateOfCreation(testCompanyName, Some(now.minusDays(70)))

      DateUtils.isCompanyRecentlyCreated(companyNameAndDateOfCreation) mustBe false
    }

    "return false for a company created over a year ago" in {
      val companyNameAndDateOfCreation = CompanyNameAndDateOfCreation(testCompanyName, Some(now.minusDays(366)))

      DateUtils.isCompanyRecentlyCreated(companyNameAndDateOfCreation) mustBe false
    }

    "return false for a company where no date of creation is found" in {
      val companyNameAndDateOfCreation = CompanyNameAndDateOfCreation(testCompanyName, None)

      DateUtils.isCompanyRecentlyCreated(companyNameAndDateOfCreation) mustBe false
    }

  }


}
