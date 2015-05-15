package com.kayad.sample.spray

import akka.actor.Actor
import spray.http.StatusCodes._
import spray.routing._
import spray.http._
import MediaTypes._

case class MyCookieValidationRejection(ck: HttpCookie) extends Rejection

class MyServiceActor3 extends Actor with MyService {
  def actorRefFactory = context
  def receive = runRoute(route)
}


trait MyService3 extends HttpService {

  implicit val myRejectionHandler = RejectionHandler {
    case MalformedQueryParamRejection(name, msg, _) :: _ =>
      respondWithMediaType(`application/json`)(
        complete(BadRequest, s"""{"error":{"message":"The query parameter $name was malformed: "$msg"}}""")
      )
    case MyCookieValidationRejection(ck) :: _ =>
      respondWithMediaType(`application/json`)(
        complete(BadRequest, s"""{"error": {"message":"My cookie value was malformed:{name: ${ck.name}; value: ${ck.content}"}}""")
      )
    case MissingCookieRejection(cookieName) :: _ ⇒
      respondWithMediaType(`application/json`)(
        complete(BadRequest, s"""{"error": {"message":"Request is missing required cookie: $cookieName"}}""")
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
