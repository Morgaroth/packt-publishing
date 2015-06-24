package io.github.morgaroth.packt.publishing

import akka.actor.ActorSystem
import org.scalatest.concurrent.Futures
import org.scalatest.{Matchers, WordSpec}
import spray.client.pipelining._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps

class FreeBook$Test extends WordSpec with Matchers with Futures {
  "FreeBook extractor" should {
    "extract link from saved html" in {
      val savedhtml = Source.fromURL(getClass.getResource("/free-learning.html")).mkString
      savedhtml match {
        case FreeBook(id) =>
          id.link should equal("/freelearning-claim/2387/21478")
        case another => fail(s"not extracted free ebook link")
      }
    }
    "extract form token from live html" in {
      implicit val ac = ActorSystem("test")
      import ac.dispatcher

      val pipe = sendReceive ~> unmarshal[FreeBook]

      val request = pipe(FreeBookIndex.request)
      request.onSuccess {
        case entity =>
          entity.link.length should be > 0
          entity.link should startWith("/freelearning-claim/")
        case another => fail(s"not extracted ebook link")
      }
      request.onFailure {
        case t: Throwable => fail(t)
      }
      Await.result(request, 40 seconds)
    }
  }

}
