package com.iws.main

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class MyActor2 extends Actor {

  implicit val materializer = ActorMaterializer()
// implicit val ec: ExecutionContext = system.dispatcher

  override def receive: Receive = {
    case _ => {
      // use context.system explicitly
      val responseFuture: Future[HttpResponse] = Http(context.system)
        .singleRequest(HttpRequest(uri = "http://localhost:8080/pets/mf"))
      responseFuture onComplete {
        case Success(res) => println(res)
        case Failure(t) => println("An error has occured: " + t.getMessage)
      }
    }
  }
}
