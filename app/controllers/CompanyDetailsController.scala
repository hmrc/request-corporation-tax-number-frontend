/*
 * Copyright 2019 HM Revenue & Customs
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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.CompanyDetailsFormProvider
import identifiers.CompanyDetailsId
import models.{CompanyDetails, Mode}
import play.api.mvc.MessagesControllerComponents
import utils.{Navigator, UserAnswers}
import views.html.companyDetails

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CompanyDetailsController @Inject()(
                                          appConfig: FrontendAppConfig,
                                          override val messagesApi: MessagesApi,
                                          dataCacheConnector: DataCacheConnector,
                                          navigator: Navigator,
                                          getData: DataRetrievalAction,
                                          formProvider: CompanyDetailsFormProvider,
                                          cc: MessagesControllerComponents
                                        ) extends FrontendController(cc) with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode) = getData {
    implicit request =>
      val preparedForm = request.userAnswers.flatMap(x => x.companyDetails) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(companyDetails(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = getData.async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(companyDetails(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[CompanyDetails](request.externalId, CompanyDetailsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(CompanyDetailsId, mode)(new UserAnswers(cacheMap))))
      )
  }
}

