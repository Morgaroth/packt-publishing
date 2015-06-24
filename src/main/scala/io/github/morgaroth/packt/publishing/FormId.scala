package io.github.morgaroth.packt.publishing

import spray.http.HttpResponse
import spray.httpx.unmarshalling.{Deserialized, FromResponseUnmarshaller, MalformedContent}

import scala.util.matching.Regex.Match

class FormId(val token: String, val id: String)

object FormId {
  val formTokenR = """id="edit\-packt\-user\-login\-form\-form\-token" value="(.*)"""".r

  val formIdR = """Forgotten your password\?</a>[\s.]+<input +type="hidden" +name="form_build_id" +id="(.+)" +value="(.+)"""".r

  def unapply(html: String): Option[FormId] = {
    val formToken: List[Match] = formTokenR.findAllMatchIn(html).toList
    val token = formToken match {
      case formTokenR(id) :: Nil => Some(id)
      case another =>
        println(s"form token : $another")
        None
    }
    val formBuild: List[Match] = formIdR.findAllMatchIn(html).toList
    val build = formBuild match {
      case formIdR(id, id2) :: Nil => Some(id)
      case another =>
        println(s"formId $another")
        None
    }
    token
      .flatMap(token => build.map(id => token -> id))
      .map(x => new FormId(x._1, x._2))
  }


  implicit lazy val unmarshaller = new FromResponseUnmarshaller[FormId] {
    override def apply(v1: HttpResponse): Deserialized[FormId] = {
      try {
        val entity: String = v1.entity.asString
        unapply(entity)
          .map(Right(_))
          .getOrElse(throw new Exception("not extracted form id from"))
      } catch {
        case e: Exception => Left(MalformedContent("malformed formId " + e.getMessage))
      }
    }
  }
}
