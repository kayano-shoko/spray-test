package com.kayad.sample.spray

import akka.actor.Actor
import spray.http.StatusCodes._
import spray.routing._
import spray.http._
import MediaTypes._

class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

trait MyService extends HttpService {
  val route =
    path("hoge") {
      get {
      parameter('id.as[Int]){ id =>
        respondWithMediaType(`application/json`) {
          complete { s"""{ "success": id is $id. }""" }
        }
      }
    }
  }
}
