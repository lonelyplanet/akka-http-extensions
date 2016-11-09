package com.lonelyplanet.akka.http.extensions.tracing

import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.{BasicDirectives, ExecutionDirectives, HeaderDirectives}
import kamon.Kamon
import kamon.trace.Tracer

import scala.util.control.NonFatal

trait KamonTracingDirectives extends BasicDirectives with ExecutionDirectives with HeaderDirectives {

  def withTraceToken(maybeTraceToken: Option[String]): Directive[Unit] = extractRequestContext.flatMap { ctx =>
    Tracer.setCurrentContext(
      Kamon.tracer.newContext(name = "undefined", token = maybeTraceToken)
    )

    mapResponse { resp =>
      clearCurrentContext()
      resp
    } & handleExceptions {
      ExceptionHandler {
        case NonFatal(e) =>
          clearCurrentContext()
          throw e
      }
    } & mapRejections { r =>
      clearCurrentContext()
      r
    }
  }

  private def clearCurrentContext() = {
    if (!Tracer.currentContext.isClosed) {
      Tracer.currentContext.finish()
      Tracer.clearCurrentContext
    }
  }
}
