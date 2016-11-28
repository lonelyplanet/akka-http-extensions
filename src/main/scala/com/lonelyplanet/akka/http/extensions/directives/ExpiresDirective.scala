package com.lonelyplanet.akka.http.extensions.directives

import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.model.headers.Expires
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.directives.BasicDirectives.mapResponse

trait ExpiresDirective {
  def expires(expirationDate: DateTime): Directive0 = {
    mapResponse(_.withDefaultHeaders(Expires(expirationDate)))
  }
}

object ExpiresDirective extends ExpiresDirective