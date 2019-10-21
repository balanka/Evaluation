package com.iws.main
import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import akka.event.Logging

class MyActor extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case "test" => log.info("received test")
    case _      => log.info("received unknown message")
  }
}

object Main {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("demo")
    val myActor = system.actorOf(Props[MyActor2])

    myActor ! "test"

  }
}