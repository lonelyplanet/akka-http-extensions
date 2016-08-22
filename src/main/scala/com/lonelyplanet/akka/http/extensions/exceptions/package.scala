package com.lonelyplanet.akka.http.extensions

package object exceptions {
  case class ResourceNotFound(message: String) extends RuntimeException(message)
}
