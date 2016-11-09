package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.lonelyplanet.akka.http.extensions.exceptions.ResourceNotFound
import com.lonelyplanet.akka.http.extensions.rejections.BadParameterRejection
import com.lonelyplanet.util.logging.Loggable
import org.zalando.jsonapi.Jsonapi
import org.zalando.jsonapi.json.akka.http.AkkaHttpJsonapiSupport._
import spray.json._

import scala.util.control.NonFatal

trait ExceptionHandling extends Loggable {

  def timeoutResponse(traceToken: Option[String]): HttpResponse = {
    HttpResponse(
      status = StatusCodes.GatewayTimeout,
      entity = HttpEntity(
        contentType = MediaTypes.`application/vnd.api+json`,
        string = Jsonapi
          .asRootObject(ErrorMessage("There was a timeout processing your request", traceToken))
          .toJson
          .compactPrint
      )
    )
  }

  implicit def rejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case BadParameterRejection(message) =>
          optionalHeaderValueByName("x-trace-token") { traceToken =>
            complete {
              (BadRequest, ErrorMessage("Invalid query parameter value", traceToken, Some(message)))
            }
          }
      }.handleAll[MethodRejection] { methodRejections ⇒
        val supportedMethods = methodRejections.map(_.supported.name)
        optionalHeaderValueByName("x-trace-token") { traceToken =>
          complete {
            (MethodNotAllowed, ErrorMessage(s"HTTP method not allowed, supported only: ${supportedMethods mkString " or "}", traceToken))
          }
        }
      }
      .handleNotFound {
        optionalHeaderValueByName("x-trace-token") { traceToken =>
          complete {
            (NotFound, ErrorMessage(s"Requested resource not found", traceToken))
          }
        }
      }
      .result()

  private def internalServerErrorResponse(traceToken: Option[String]) = {
    (InternalServerError, ErrorMessage(s"Error occurred while processing the request", traceToken))
  }

  private def resourceNotFoundResponse(message: String, traceToken: Option[String]) = {
    (NotFound, ErrorMessage(message, traceToken))
  }

  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: ResourceNotFound =>
        optionalHeaderValueByName("x-trace-token") { traceToken =>
          complete(resourceNotFoundResponse(e.getMessage, traceToken))
        }
      case NonFatal(e) =>
        optionalHeaderValueByName("x-trace-token") { traceToken =>
          extractUri { uri =>
            logger.error(s"Request to $uri could not be handled", e)
            complete(internalServerErrorResponse(traceToken))
          }
        }
    }
}
