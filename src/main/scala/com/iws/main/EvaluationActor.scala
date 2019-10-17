package com.iws.main

  import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder, ResolverStyle}

  import akka.actor.{Actor, ActorLogging, Props}
  import com.iws.main.WebServer.{fetchLeastWordy, fetchMostSecurity, fetchMostSpeeches}
  import com.iws.model.{RequestParameter, Result, Speech}

  import scala.util.Try


  object EvaluationActor {

    final case class ExecuteRequest(param:RequestParameter)


    def props: Props = Props[EvaluationActor]
  }

  class EvaluationActor extends Actor with ActorLogging {
    import EvaluationActor._

    //var speeches = Set.empty[Speech]
    val speechesx=List(
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


    def receive: Receive = {

      case ExecuteRequest(request) => {

        val mostSpeeches :String= Try(fetchMostSpeeches(speechesx, request.year)).getOrElse("null")
        val mostSecurity:String = Try(fetchMostSecurity(speechesx, request.theme)).getOrElse("null")
        val leastWordy:String = Try(fetchLeastWordy(speechesx,request.words)).getOrElse("null")

        val result:Result= Result(mostSpeeches, mostSecurity, leastWordy)
        sender() ! Result("null", "null", "null")
      }

    }
  }

