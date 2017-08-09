package uk.gov.hmrc.play.frontend.filters

import com.ning.http.util.Base64
import play.api.Logger
import play.api.mvc.RequestHeader

import scala.util.Try

object DeviceFingerprint {

  val deviceFingerprintCookieName = "mdtpdf"

  def deviceFingerprintFrom(request: RequestHeader): String =
    request.cookies.get(deviceFingerprintCookieName).map { cookie =>
      val decodeAttempt = Try {
        Base64.decode(cookie.value)
      }
      decodeAttempt.failed.foreach { e => Logger.info(s"Failed to decode device fingerprint '${cookie.value}' caused by '${e.getClass.getSimpleName}:${e.getMessage}'")}
      decodeAttempt.map {
        new String(_, "UTF-8")
      }.getOrElse("-")
    }.getOrElse("-")

}