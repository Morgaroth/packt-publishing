package io.github.morgaroth.packt.publishing

import akka.actor.ActorSystem
import org.scalatest.concurrent.Futures
import org.scalatest.{Matchers, WordSpec}
import spray.client.pipelining._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps

class FormId$Test extends WordSpec with Matchers with Futures {

  "FormId extractor" should {
    "extract form id from saved html" in {
      val savedhtml = Source.fromURL(getClass.getResource("/packt_pub.html")).mkString
      savedhtml match {
        case FormId(id) => id should equal("bac250255949cd17f80df224dea24bce")
        case another => fail(s"not extracted form id")
      }
    }
    "extract form token from live html" in {
      implicit val ac = ActorSystem("test")
      import ac.dispatcher

      val pipe = sendReceive

      val request: Future[String] = pipe(SiteIndex.request).map(_.entity.asString)
      request.onSuccess {
        case entity => entity match {
          case FormId(id) => id.length should be > 0
          case another => fail(s"not extracted form id")
        }
      }
      request.onFailure {
        case t: Throwable => fail(t)
      }
      Await.result(request, 40 seconds)
    }
  }

}
