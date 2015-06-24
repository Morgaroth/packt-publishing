package io.github.morgaroth.packt.publishing.commands

import akka.actor.ActorSystem
import io.github.morgaroth.packt.publishing.commands.Claim._
import io.github.morgaroth.packt.publishing.{MainLogic, core}

object Login extends App with core with MainLogic {
  override def system: ActorSystem = ActorSystem("login")

  loadIndex.flatMap(login(args(0), args(1))).onComplete { x =>
    println(x)
    system.shutdown()
  }
}