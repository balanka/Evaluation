package  com.iws.main

import java.time.format.{DateTimeFormatterBuilder, ResolverStyle}

import com.iws.model._
import java.time.format.DateTimeFormatter

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.http.scaladsl.unmarshalling.PredefinedFromStringUnmarshallers._
import Directives._
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import com.iws.model._
//import nequi.zio.logger._
//import com.github.mlangc.slf4zio.api._
import zio.ZIO
//import zio.stream.Stream


import scala.util.{Right, Try}
import scala.io.StdIn

object WebServer {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  val formatter:DateTimeFormatter  = new DateTimeFormatterBuilder()
    .parseStrict()
    .appendPattern("uuuu-MM-dd")
    .toFormatter()
    .withResolverStyle(ResolverStyle.STRICT);

  def  filterByYear(year:Int, speech:Speech):Boolean = {
    val d = java.time.LocalDate.parse(speech.day.trim, formatter)
    d.getYear  == year
  }

  def  getYear(speech:Speech):Int = {
    val d = java.time.LocalDate.parse(speech.day.trim, formatter)
    d.getYear
  }




  def fetchMostSpeeches(col:List[Speech], year: Int):String = {
    val l = col.filter(filterByYear(year, _))
           .map(x => (x.speaker,getYear(x)))
           .groupBy(_._1)
           .mapValues(_.map(_._2).sum/year)
           .maxBy(_._2)
    //println("l.1:"+l4._1)
           l._1

  }
  def fetchMostSecurity(col:List[Speech], filter:String) = {
    val l = col.filter(_.theme.trim.contains(filter.trim))
      .map(x => (x.speaker, x.theme))
      .groupBy(_._1)
      .mapValues(_.map(_._2.size).sum/filter.trim.size)
      .maxBy(_._2)

   // println("l.1:"+l)

    l._1
  }
  def fetchLeastWordy(col:List[Speech], filter:Int):String = {
    val l = col.filter(_.words <=filter)
      .map(x => (x.speaker, x.words))
      .groupBy(_._1)
      .mapValues(_.map(_._2).sum)
      .minBy(_._2)

   // println("l.1:"+l)
    l._1
  }


  implicit def evaluationRejectionHandler =
    RejectionHandler.newBuilder()
      .handleNotFound {
        extractUnmatchedPath { p =>
          complete((NotFound, s"The path you requested [${p}] does not exist."))
        }
      }
      .result()


  def main(args: Array[String]) {

      /*var speechesx=List(
      Speech("Alexander Abel", "Bildungspolitik", "2012-10-30", 5310),
      Speech("Alexander Abel", "Bildungspolitik", "2012-10-30", 5310),
      Speech("Bernhard Belling", "Kohlesubventionen", "2012-11-05", 1210),
      Speech("Bernhard Belling", "Kohlesubventionen", "2012-11-05", 1210),
      Speech("Bernhard Belling", "Kohlesubventionen", "2012-11-05", 1210),
      Speech("Bernhard Belling", "Innere Sicherheit", "2012-11-05", 1210),
      Speech("Caesare Collins", "Kohlesubventionen", "2012-11-06", 59),
      Speech("Caesare Collins", "Kohlesubventionen", "2012-11-06", 59),
      Speech("Alexander Abel", "Innere Sicherheit", "2012-12-11", 911),
      Speech("Alexander Abel", "Innere Sicherheit", "2012-12-11", 911),
      Speech("Bernhard Belling", "Innere Sicherheit", "2012-12-11", 911),
      Speech("Bernhard Belling", "Innere Sicherheit", "2012-12-11", 911)
    )

   for {
      q <- Queue.bounded[String](10)
      _ <- q.offer("a")
      _ <- q.offer("b")
      s <- Stream.fromQueue(q).foreach(a => putStrLn(a)).fork
      _ <- q.offer("c")
      _ <- q.offer("d")
      _ <- s.await
    } yield 0

     */
      // val url:java.net.URL = getClass.getResource("/Users/iwsmac/Downloads/tools/scala/samples/akka/Evaluation/src/main/resources/20190807-43006329-umsatz.CSV")
      def list2Tuple1(x: List[String]): List[String] =
      x match {
      case List (a, b, c, d, e, f, g, h, i, j, k) =>
           List (a, b, c, d, e, f, g, h, i, j, k)
    }
    def tuple2BankStatement(x: List[String]): List[BankStatement] =
    x match {
    case List (a, b, c, d, e, f, g, h, i, j, k) =>
         List (BankStatement (a, b, c, d, e, f, g, h, i, j, k))
    }


    val rawDataList = scala.io.Source.fromFile("/Users/iwsmac/Downloads/20190807-43006329-umsatz.CSV").getLines.toList
     val data = rawDataList.map(_.split(";").toList) match {
                                 case x :: xs => xs.flatMap(tuple2BankStatement)
                              }
    data.foreach(println)
    System.exit(0)
    //logged.ensuring()

    //val r =iter.right
    //val rawData=scala.io.Source.fromURL(url).mkString
    //val bs = rawData.asCsvReader[BankStatement](rfc.withHeader).collect { case Right(a) ⇒ a }.toList
    //bs.foreach(println)
    //println("BS:"+bs)
   // }
    //System.exit(0)
   // val rawData=scala.io.Source.fromURL("https://dev-stefan.s3.amazonaws.com/politics.csv").mkString
   // val speeches = rawData.asCsvReader[Speech](rfc.withHeader).collect { case Right(a) ⇒ a }.toList
    val speeches=List.empty[Speech]

    val route: Route =
      concat(
        get {
         // yearRoute
          pathPrefix("evaluation") {
              parameters(('year.as[Int], 'theme.as[String] , 'words.as[Int]))
                .as(RequestParameter) { param =>
                  val mostSpeeches :String= Try(fetchMostSpeeches(speeches, param.year)).getOrElse("null")
                  val mostSecurity:String = Try(fetchMostSecurity(speeches, param.theme)).getOrElse("null")
                  val leastWordy:String = Try(fetchLeastWordy(speeches,param.words)).getOrElse("null")

                  val result:Result= Result(mostSpeeches, mostSecurity, leastWordy)
                    println("Result of success case" + result)
                    complete(result)
                  }
                }

        } ,


        pathPrefix("handled") {
          handleRejections(evaluationRejectionHandler) {
            path("existing")(complete("This path exists")) ~
              path("boom")(reject(new ValidationRejection("This didn't work.")))
          }
        }

      )
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }
}

  


