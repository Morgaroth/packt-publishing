package io.github.morgaroth.packt.publishing

import akka.actor.ActorSystem
import org.scalatest.concurrent.Futures
import org.scalatest.{Matchers, WordSpec}
import spray.client.pipelining._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps

class FormId$Test extends WordSpec with Matchers with Futures {

  "FormId extractor" should {
    "extract form id from saved html" in {
      val savedhtml = Source.fromURL(getClass.getResource("/packt_pub.html")).mkString
      savedhtml match {
        case FormId(id) =>
          id.token should equal("9a52e59379d164398f78d64b82f62982")
          id.id should equal("form-f9a0a919920160f81ac073e2c0b0294e")
        case another => fail(s"not extracted form id")
      }
    }
    "extract form token from live html" in {
      implicit val ac = ActorSystem("test")
      import ac.dispatcher

      val pipe = sendReceive ~> unmarshal[FormId]

      val request = pipe(SiteIndex.request)
      request.onSuccess {
        case entity =>
          entity.token.length should be > 0
          entity.id.length should be > 0
        case another => fail(s"not extracted form id")
      }
      request.onFailure {
        case t: Throwable => fail(t)
      }
      Await.result(request, 40 seconds)
    }
  }

}
