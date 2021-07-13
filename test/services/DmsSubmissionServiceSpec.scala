/*
 * Copyright 2021 HM Revenue & Customs
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

package services

import base.SpecBase
import connectors.CtutrConnector
import models._
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.audit.DefaultAuditConnector
import utils.MockUserAnswers

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class DmsSubmissionServiceSpec extends SpecBase with ScalaFutures {

  implicit val hcCaptor = ArgumentCaptor.forClass(classOf[HeaderCarrier])
  implicit val ecCaptor = ArgumentCaptor.forClass(classOf[ExecutionContext])

  ".ctutrSubmission" when {

    "the submission is successful" must {

      "return success" in {
        val mockAuditConnector = mock(classOf[DefaultAuditConnector])

        val answers = MockUserAnswers.minimalValidUserAnswers

        val mockCtutrConnector = mock(classOf[CtutrConnector])
        when(mockCtutrConnector.ctutrSubmission(any())(any(), any())) thenReturn Future.successful(Some(SubmissionResponse("id", "filename")))

        val service = new DmsSubmissionService(mockCtutrConnector, mockAuditConnector)
        implicit val hc: HeaderCarrier = new HeaderCarrier

        val futureResult = service.ctutrSubmission(answers)

        whenReady(futureResult, Timeout(5 seconds)) { result =>
          result mustBe SubmissionSuccessful
        }
      }
    }

    "the submission fails" must {

      "return failure" in {
        val mockAuditConnector = mock(classOf[DefaultAuditConnector])

        val answers = MockUserAnswers.minimalValidUserAnswers

        val mockCtutrConnector = mock(classOf[CtutrConnector])
        when(mockCtutrConnector.ctutrSubmission(any())(any(), any())) thenReturn Future.successful(None)

        val service = new DmsSubmissionService(mockCtutrConnector, mockAuditConnector)
        implicit val hc: HeaderCarrier = new HeaderCarrier

        val futureResult = service.ctutrSubmission(answers)

        whenReady(futureResult) { result =>
          result mustBe SubmissionFailed
        }
      }
    }
  }
}
