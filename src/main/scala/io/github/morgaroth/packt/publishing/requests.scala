package io.github.morgaroth.packt.publishing

import spray.client.pipelining._

import scala.util.matching.Regex.Match

object SiteIndex {
  def request = Get("https://www.packtpub.com/")
}

object LoginPost {
  def request = Post("https://www.packtpub.com/")
}

object FormId {
  val formTokenR = """.*<input type="hidden" name="form_token" id="edit\-packt\-user\-login\-form\-form\-token" value="(.*)".*""".r
  val regex2 = """.*<input type="hidden" name="form_build_id" id="(.*)" value="(.*)">.*""".r

  def unapply(html: String): Option[String] = {
    val formToken: List[Match] = formTokenR.findAllMatchIn(html).toList
    val token = formToken match {
      case formTokenR(id) :: Nil => Some(id)
      case _ => None
    }
    val formBuild: List[Match] = regex2.findAllMatchIn(html).toList
    val build = formBuild match {
      case regex2(id, id2) :: Nil if id == id2 => Some(id)
      case regex2(id, id2) :: Nil =>
        // todo warning
        Some(id2)
      case _ => None
    }
    token
  }
}
