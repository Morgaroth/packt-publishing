package io.github.morgaroth.packt.publishing

import akka.actor.{ActorRefFactory, ActorSystem}
import spray.client.pipelining._
import spray.http.HttpResponse

import scala.concurrent.{Future, ExecutionContext}

trait core {
  def system: ActorSystem

  implicit def executor: ExecutionContext = system.dispatcher

  implicit def refFactory: ActorRefFactory = system
}

trait MainLogic {
  this: core =>

  def loadIndex: Future[FormId] = {
    val pipe = sendReceive ~> unmarshal[FormId]
    pipe(SiteIndex.request)
  }

  def login(email: String, pass: String)(id: FormId): Future[UserInfo] = {
    val pipe = sendReceive
    pipe(LoginPost.request(LoginData(id, email, pass)))
  }.map { resp =>
    resp.headers.find(_.is("set-cookie")).map(_.value.split(";").head.split("=").last).get
  }.map(UserInfo)


  def getLink: Future[FreeBook] = {
    val pipe = sendReceive ~> unmarshal[FreeBook]
    pipe(FreeBookIndex.request)
  }

  def loadSession(email: String, hashedPassword: String): Future[UserInfo] = {
    loadIndex.flatMap(login(email, hashedPassword))
  }

  def claimBook(session: Future[UserInfo]) = {
    val pipe = sendReceive
    session.zip(getLink).flatMap(x => pipe(FreeBookPost.request(x._1, x._2))).flatMap {
      resp => resp.status.intValue match {
        case succ if succ < 400 => Future.successful(succ)
        case bad => Future.failed(new ClaimBookHttpException(resp))
      }
    }
  }
}

class ClaimBookHttpException(response: HttpResponse) extends Exception