package io.github.morgaroth.packt.publishing.commands

import akka.actor.ActorSystem
import io.github.morgaroth.packt.publishing.{MainLogic, core}

object Claim extends core with MainLogic {
  override def system: ActorSystem = ActorSystem("claim")

  def main(args: Array[String]) {
    val session = loadSession(args(0), args(1))
    claimBook(session).onComplete { x =>
      println(x)
      system.shutdown()
    }
  }
}
