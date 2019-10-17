package com.iws.main

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import scala.concurrent.Future
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global

class MyActor extends Actor {

  implicit val materializer = ActorMaterializer()
// implicit val ec: ExecutionContext = system.dispatcher

  override def receive: Receive = {
    case _ => {
      // use context.system explicitly
      val responseFuture: Future[HttpResponse] = Http(context.system)
        .singleRequest(HttpRequest(uri = "http://localhost:8090"))
      responseFuture onComplete {
        case Success(res) => println(res)
        case Failure(t) => println("An error has occured: " + t.getMessage)
      }
    }
  }
}
