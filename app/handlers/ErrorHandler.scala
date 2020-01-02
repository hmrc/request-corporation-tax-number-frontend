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

package handlers

import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.http.FrontendErrorHandler
import views.html.{ErrorTemplateInternalServerErrorView, ErrorTemplateNotFoundView, ErrorTemplateView}

@Singleton
class ErrorHandler @Inject()(
                              notFoundView: ErrorTemplateNotFoundView,
                              internalServerErrorView: ErrorTemplateInternalServerErrorView,
                              view: ErrorTemplateView,
                              val messagesApi: MessagesApi
                            ) extends FrontendErrorHandler with I18nSupport {

  override def notFoundTemplate(implicit request: Request[_]): Html = {
    notFoundView(
      Messages("error.pageNotFound.title"),
      Messages("error.pageNotFound.heading"),
      Messages("error.pageNotFound.message1"),
      Messages("error.pageNotFound.message2"),
      Messages("error.pageNotFound.message3"),
      Messages("error.pageNotFound.messageLink"))
  }

  override def internalServerErrorTemplate(implicit request: Request[_]): Html = {
    internalServerErrorView(
      Messages("error.internalError.title"),
      Messages("error.internalError.heading"),
      Messages("error.internalError.message1"))
  }


  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit rh: Request[_]): Html =
    view(pageTitle, heading, message)
}

