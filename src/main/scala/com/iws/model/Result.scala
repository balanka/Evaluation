package com.iws.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class Result(
  mostSpeeches:String,
  mostSecurity:String,
  leastWordy: String 
)

object Result {
  implicit val resultJsonFormat: RootJsonFormat[Result] = jsonFormat3(Result.apply)
}
