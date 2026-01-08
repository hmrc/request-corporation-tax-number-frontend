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

package handlers

import base.SpecBase
import play.api.i18n.{Messages, MessagesApi}
import views.html.{ErrorTemplateInternalServerErrorView, ErrorTemplateNotFoundView, ErrorTemplateView}

class ErrorHandlerSpec extends SpecBase {

  private val messageApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  private val errorTemplate: ErrorTemplateView = app.injector.instanceOf[ErrorTemplateView]
  private val errorNotFoundTemplate: ErrorTemplateNotFoundView = app.injector.instanceOf[ErrorTemplateNotFoundView]
  private val errorInternalServerTemplate: ErrorTemplateInternalServerErrorView = app.injector.instanceOf[ErrorTemplateInternalServerErrorView]
  private val errorHandler: ErrorHandler = new ErrorHandler(errorNotFoundTemplate, errorInternalServerTemplate, errorTemplate, messageApi)

  "ErrorHandler" must {

    "return an error page" in {
      val result = await(errorHandler.standardErrorTemplate(
        pageTitle = "pageTitle",
        heading = "heading",
        message = "message"
      )(fakeRequest))

      result.body must include("pageTitle")
      result.body must include("heading")
      result.body must include("message")
    }

    "return a not found template" in {
      val result = await(errorHandler.notFoundTemplate(fakeRequest))

      val pageNotFoundTitle = Messages("error.pageNotFound.title")(messages)
      val pageNotFoundHeading = Messages("error.pageNotFound.heading")(messages)
      val pageNotFoundMessage1 = Messages("error.pageNotFound.message1")(messages)
      val pageNotFoundMessage2 = Messages("error.pageNotFound.message2")(messages)
      val pageNotFoundMessage3 = Messages("error.pageNotFound.message3")(messages)
      val pageNotFoundMessageLink = Messages("error.pageNotFound.messageLink")(messages)

      result.body must include(pageNotFoundTitle)
      result.body must include(pageNotFoundHeading)
      result.body must include(pageNotFoundMessage1)
      result.body must include(pageNotFoundMessage2)
      result.body must include(pageNotFoundMessage3)
      result.body must include(pageNotFoundMessageLink)
    }

    "return an internal server error template" in {
      val result = await(errorHandler.internalServerErrorTemplate(fakeRequest))

      val internalErrorTitle = Messages("error.internalError.title")(messages)
      val internalErrorHeading = Messages("error.internalError.heading")(messages)
      val internalErrorMessage1 = Messages("error.internalError.message1")(messages)

      result.body must include(internalErrorTitle)
      result.body must include(internalErrorHeading)
      result.body must include(internalErrorMessage1)
    }
  }
}
