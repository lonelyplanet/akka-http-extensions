package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.directives.BasicDirectives._
import com.lonelyplanet.akka.http.extensions.exceptions.ResourceNotFound
import com.lonelyplanet.util.AirbrakeService

trait ExceptionHandlingWithAirbrake extends ExceptionHandling {
  val airbrakeService: AirbrakeService
  val airbrakeComponent: String = ExceptionHandlingWithAirbrake.DefaultAirbrakeComponent

  override implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: ResourceNotFound =>
      super.exceptionHandler.apply(e)
    case e =>
      extractUri { uri =>
        airbrakeService.notify(e, uri.toString(), airbrakeComponent)
        super.exceptionHandler.apply(e)
      }
  }
}

object ExceptionHandlingWithAirbrake {
  val DefaultAirbrakeComponent = "api"
}
