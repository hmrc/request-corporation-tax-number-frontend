/*
 * Copyright 2022 HM Revenue & Customs
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

package http

import akka.actor.ActorSystem
import com.typesafe.config.Config
import javax.inject.Inject
import play.api.libs.ws.{WSRequest => PlayWSRequest}
import play.api.libs.ws.{WSClient, WSProxyServer}
import play.api.{Configuration, Logging}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.http.ws.{WSHttp, WSProxy, WSProxyConfiguration}

import scala.concurrent.duration._

class ProxyHttpClient @Inject()(val auditConnector: AuditConnector,
                                 val actorSystem: ActorSystem,
                                 config: Configuration,
                                 client: WSClient
                               ) extends HttpClient with WSHttp with HttpAuditing with WSProxy with Logging {

  def buildRequest[A](url: String, headers: Seq[(String, String)] = Seq.empty)
                              (implicit hc: HeaderCarrier): PlayWSRequest = {
    if(wsProxyServer.nonEmpty) logger.info("proxy enabled with configuration")
    super.buildRequest(url, headers).withRequestTimeout(60.seconds)
  }

  override def configuration: Config = config.underlying

  override val appName: String = config.get[String]("appName")

  override val hooks: Seq[HttpHook] = Seq(AuditingHook)

  override def wsProxyServer: Option[WSProxyServer] = WSProxyConfiguration("proxy", config)

  override def wsClient: WSClient = client
}
