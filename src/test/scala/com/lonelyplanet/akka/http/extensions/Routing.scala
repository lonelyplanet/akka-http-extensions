package com.lonelyplanet.akka.http.extensions
import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.lonelyplanet.akka.http.extensions.exceptions.ResourceNotFound

import scala.concurrent.Future

class Routing extends ExceptionHandling {
  implicit val system = ActorSystem("test")
  implicit val executionContext = system.dispatcher

  val route = Route.seal {
    path("") {
      get {
        complete {
          "ok"
        }
      }
    } ~ path("exception") {
      get {
        complete {
          Future {
            throw new RuntimeException("exception")
            "should not happen"
          }
        }
      }
    } ~ path("resource") {
      get {
        complete {
          Future {
            throw new ResourceNotFound("resource not found")
            "should not happen"
          }
        }
      }
    }
  }
}

