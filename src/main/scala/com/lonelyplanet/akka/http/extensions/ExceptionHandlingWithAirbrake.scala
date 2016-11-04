package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.server.ExceptionHandler
import com.lonelyplanet.akka.http.extensions.exceptions.ResourceNotFound
import com.lonelyplanet.util.AirbrakeService

trait ExceptionHandlingWithAirbrake extends ExceptionHandling {
  val airbrakeService: AirbrakeService

  override implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: ResourceNotFound =>
      super.exceptionHandler.apply(e)
    case e =>
      airbrakeService.notify(e)
      super.exceptionHandler.apply(e)
  }
}
