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

package uk.gov.hmrc.play.frontend.filters

import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import play.api.http.HeaderNames
import play.api.mvc.{Cookie, Cookies, RequestHeader, Result, Results}
import play.api.test.{FakeApplication, FakeRequest, WithApplication}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.filters.frontend.{DeviceId, DeviceIdCookie}
import org.apache.commons.codec.binary.Base64

import scala.concurrent.Future

class DeviceIdCookieFilterSpec extends WordSpecLike with Matchers with MockitoSugar with ScalaFutures with OptionValues {

  final val theSecret = "some_secret"
  final val thePreviousSecret = "some previous secret with spaces since spaces cause an issue unless encoded!!!"
  final val previousSecret = new String(Base64.encodeBase64(thePreviousSecret.getBytes()))
  final val previousSecretValues = Seq(previousSecret)

  lazy val createDeviceId = new DeviceIdCookie {
    override val secret = theSecret
    override val previousSecrets = previousSecretValues
  }

  val appConfig = Map("cookie.deviceId.secret" -> theSecret, "cookie.deviceId.previous.secret" -> previousSecretValues)
  val appConfigNoPreviousKey = Map("cookie.deviceId.secret" -> theSecret)

  val auditConnector = mock[AuditConnector]

  trait Setup {
    val action = {
      val mockAction = mock[(RequestHeader) => Future[Result]]
      val outgoingResponse = Future.successful(Results.Ok)
      when(mockAction.apply(any())).thenReturn(outgoingResponse)
      mockAction
    }

    def requestPassedToAction: RequestHeader = {
      val updatedRequest = ArgumentCaptor.forClass(classOf[RequestHeader])
      verify(action).apply(updatedRequest.capture())
      updatedRequest.getValue
    }
  }

  def mdtpdiSetCookie(result: Result): Cookie = {
    val cookie = for {
      header <- result.header.headers.get("Set-Cookie")
      setCookies = Cookies.decodeSetCookieHeader(header)
      deviceCookie <- setCookies.find(_.name == DeviceId.MdtpDeviceId)
    }
    yield deviceCookie
    cookie.value
  }

  "DeviceIdFilter" should {

    "create the deviceId when no cookie exists" in new WithApplication(FakeApplication(additionalConfiguration = appConfig)) with Setup {

      val incomingRequest = FakeRequest()
      val response = DeviceIdCookieFilter("someapp",auditConnector)(action)(incomingRequest).futureValue

      val deviceIdRequestCookie: Cookie = requestPassedToAction.cookies(DeviceId.MdtpDeviceId)

      val responseDeviceIdCookie = mdtpdiSetCookie(response)
      responseDeviceIdCookie.value shouldBe deviceIdRequestCookie.value
      responseDeviceIdCookie.secure shouldBe true
    }

    "create the deviceId when no cookie exists and previous keys are empty" in new WithApplication(FakeApplication(additionalConfiguration = appConfigNoPreviousKey)) with Setup {

      val incomingRequest = FakeRequest()
      val response = DeviceIdCookieFilter("someapp",auditConnector)(action)(incomingRequest).futureValue

      val deviceIdRequestCookie: Cookie = requestPassedToAction.cookies(DeviceId.MdtpDeviceId)

      val responseDeviceIdCookie = mdtpdiSetCookie(response)
      responseDeviceIdCookie.value shouldBe deviceIdRequestCookie.value
      responseDeviceIdCookie.secure shouldBe true
    }


    "update the cookie even if it already exists to ensure it is secure" in new WithApplication(FakeApplication(additionalConfiguration = appConfig)) with Setup {

      val deviceId = createDeviceId.buildNewDeviceIdCookie()
      val incomingRequest = FakeRequest().withCookies(deviceId)

      val response = DeviceIdCookieFilter("someapp",auditConnector)(action)(incomingRequest).futureValue

      val deviceIdRequestCookie: Cookie = requestPassedToAction.cookies(DeviceId.MdtpDeviceId)
      deviceIdRequestCookie.value shouldBe deviceId.value

      val responseDeviceIdCookie = mdtpdiSetCookie(response)
      responseDeviceIdCookie.value shouldBe deviceIdRequestCookie.value
      responseDeviceIdCookie.secure shouldBe true
    }

    "successfully decode a deviceId generated from a previous secret" in new WithApplication(FakeApplication(additionalConfiguration = appConfig)) with Setup {

      val uuid = createDeviceId.generateUUID
      val timestamp = createDeviceId.getTimeStamp
      val deviceIdMadeFromPrevKey = DeviceId(uuid, Some(timestamp), DeviceId.generateHash(uuid, Some(timestamp), thePreviousSecret))
      val cookieDeviceIdPrevious = createDeviceId.makeCookie(deviceIdMadeFromPrevKey)
      val incomingRequest = FakeRequest().withCookies(cookieDeviceIdPrevious)

      val response = DeviceIdCookieFilter("someapp",auditConnector)(action)(incomingRequest).futureValue
      val deviceIdRequestCookie: Cookie = requestPassedToAction.cookies(DeviceId.MdtpDeviceId)

      deviceIdRequestCookie.value shouldBe cookieDeviceIdPrevious.value

      val responseDeviceIdCookie = mdtpdiSetCookie(response)
      responseDeviceIdCookie.value shouldBe deviceIdRequestCookie.value
      responseDeviceIdCookie.secure shouldBe true
    }
  }

}
