package io.github.morgaroth.packt.publishing

import spray.http.HttpResponse
import spray.httpx.unmarshalling._

import scala.util.matching.Regex.Match

case class FreeBook(link: String)

object FreeBook {

  val regex = """(/freelearning-claim/\d+/\d+)"""".r

  def unapply(html: String) = {
    val matchResult: List[Match] = regex.findAllMatchIn(html).toList
    matchResult match {
      case regex(link) :: Nil => Some(new FreeBook(link))
      case another =>
        println(s"invalid: $another")
        None
    }
  }

  implicit lazy val unmarshaller = new FromResponseUnmarshaller[FreeBook] {
    override def apply(v1: HttpResponse): Deserialized[FreeBook] = {
      try {
        val entity: String = v1.entity.asString
        unapply(entity)
          .map(Right(_))
          .getOrElse(throw new Exception("not extracted free book link"))
      } catch {
        case e: Exception => Left(MalformedContent("malformed freebook site " + e.getMessage))
      }
    }
  }
}
