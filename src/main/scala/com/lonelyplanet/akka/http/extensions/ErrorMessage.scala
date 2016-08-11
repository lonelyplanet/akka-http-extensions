package com.lonelyplanet.akka.http.extensions

import org.zalando.jsonapi.JsonapiRootObjectWriter
import org.zalando.jsonapi.model.{Error, RootObject}

case class ErrorMessage(message: String, id: Option[String])

object ErrorMessage {
  implicit val errorMessageWriter = new JsonapiRootObjectWriter[ErrorMessage] {
    override def toJsonapi(message: ErrorMessage): RootObject = {
      RootObject(
        errors = Some(List(Error(
          title = Some(message.message),
          id = Some(message.id.getOrElse("undefined"))
        )))
      )
    }
  }
}
