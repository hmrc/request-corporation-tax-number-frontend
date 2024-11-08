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

package controllers

import com.google.inject.Inject
import connectors.CompanyHouseConnector
import controllers.actions.{DataRequiredAction, DataRetrievalAction}
import models.{Submission, SubmissionSuccessful}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.SubmissionService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import java.time.{LocalDate, ZoneId}

class CheckYourAnswersController @Inject()(override val messagesApi: MessagesApi,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           cc: MessagesControllerComponents,
                                           companyHouseConnector: CompanyHouseConnector,
                                           submissionService: SubmissionService,
                                           view: CheckYourAnswersView)
                                          (implicit executionContext: ExecutionContext)
  extends FrontendController(cc) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>

      val cyaHelper = new CheckYourAnswersHelper(request.userAnswers)

      val result: Option[Result] = for {
        reference <- cyaHelper.companyDetailsReference
        name      <- cyaHelper.companyDetailsName
      } yield {

        val answerSection = AnswerSection(None, Seq(reference, name))

        Ok(view(answerSection))
      }

      result.getOrElse(Redirect(routes.SessionController.onPageLoad()))
  }

  def onSubmit(): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      companyHouseConnector.validateCRNAndReturnCompanyDetails(Submission(request.userAnswers).companyDetails).flatMap {
        case Some((false, _)) =>
          Future.successful(Redirect(routes.CompanyDetailsNoMatchController.onPageLoad()))
        case Some((true, Some(companyDetailsWithDateOfCreation)))
          if companyDetailsWithDateOfCreation.dateOfCreation.compareTo(LocalDate.now(ZoneId.of("GMT"))) <= 7 =>

          Future.successful(Redirect(routes.CompanyRegisteredController.onPageLoad()))
        case Some((true, _)) =>
          submissionService.ctutrSubmission(request.userAnswers).map {
            case SubmissionSuccessful =>
              Redirect(routes.ConfirmationController.onPageLoad())
            case _ =>
              Redirect(routes.FailedToSubmitController.onPageLoad())
          }
        case _ =>
          Future.successful(Redirect(routes.FailedToSubmitController.onPageLoad()))
      }
  }

}
