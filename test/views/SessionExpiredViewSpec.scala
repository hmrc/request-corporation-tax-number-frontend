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

package views

import controllers.routes
import views.behaviours.ViewBehaviours
import views.html.SessionExpiredView

class SessionExpiredViewSpec extends ViewBehaviours {

  def call = routes.IndexController.onPageLoad
  val view = app.injector.instanceOf[SessionExpiredView]

  def createView = () => view(call)(fakeRequest, messages)

  "Session Expired view" must {

    behave like normalPage(createView, "session_expired", "guidance")
  }
}
