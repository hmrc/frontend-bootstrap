/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.play.frontend.controller

import play.api.http.MimeTypes
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.LoggingDetails
import uk.gov.hmrc.play.audit.HeaderCarrierConverter
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext

import scala.concurrent._

trait FrontendController extends Controller with Utf8MimeTypes {

  implicit def hc(implicit request: Request[_]): HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

  implicit def mdcExecutionContext(implicit loggingDetails: LoggingDetails): ExecutionContext = MdcLoggingExecutionContext.fromLoggingDetails

  implicit class SessionKeyRemover(result: Future[Result]) {
    def removeSessionKey(key: String)(implicit request: Request[_]) = result.map {_.withSession(request.session - key)}
  }

}

trait Utf8MimeTypes {
  self : Controller =>

  override val JSON = s"${MimeTypes.JSON};charset=utf-8"

  override def HTML(implicit codec: Codec) = s"${MimeTypes.HTML};charset=utf-8"
}
