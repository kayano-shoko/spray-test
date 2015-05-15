package com.kayad.sample.spray

import akka.actor.Actor
import spray.http.StatusCodes._
import spray.routing._
import spray.http._
import MediaTypes._

class MyServiceActor2 extends Actor with MyService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

trait MyService2 extends HttpService {

  implicit val myRejectionHandler = RejectionHandler {
    case MalformedQueryParamRejection(name, msg, _) :: _ =>
      respondWithMediaType(`application/json`)(
        complete(BadRequest, s"""{"error":{"message":"The query parameter $name was malformed: "$msg"}}""")
      )
  }

  val route =
    path("hoge") {
      get {
        cookie("mycookie") { mycookie =>
          if (mycookie.content.startsWith("my"))
            respondWithMediaType(`application/json`) { complete { s"""{ "success": my cookie is ${mycookie.content}" }""" } }
          else
            reject(MyCookieValidationRejection(mycookie))
        }
      }
    }
}
