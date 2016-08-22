package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.server.Rejection

package object rejections {
  case class BadParameterRejection(errorMessage: String) extends Rejection
}
