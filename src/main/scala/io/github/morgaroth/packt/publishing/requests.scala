package io.github.morgaroth.packt.publishing

import spray.client.pipelining._
import spray.http.HttpHeaders.Cookie
import spray.http.{HttpRequest, HttpCookie, HttpHeaders, FormData}

import scala.util.matching.Regex.Match

object SiteIndex {
  def request = Get("https://www.packtpub.com/")
}

object LoginPost {
  def request(data: LoginData) =
    Post("https://www.packtpub.com/", FormData(Map(
      "email" -> data.email,
      "password" -> data.password,
      "form_build_id" -> data.formId.token,
      "form_id" -> data.formId.id,
      "op" -> "Login"
    )))
}

object FreeBookIndex {
  def request =
    Get("https://www.packtpub.com/packt/offers/free-learning")
}

object FreeBookPost {
  def request(data: UserInfo, book: FreeBook): HttpRequest = {
    Get(s"http://www.packtpub.com${book.link}")
      .withHeaders(Cookie(HttpCookie("SESS_live", data.sessionToken)))
  }
}