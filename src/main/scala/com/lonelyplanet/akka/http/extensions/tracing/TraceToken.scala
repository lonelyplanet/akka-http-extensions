package com.lonelyplanet.akka.http.extensions.tracing

import java.util.UUID

import com.lonelyplanet.akka.http.extensions.TraceTokenHeader

trait MaybeTraceTokenHolder {
  def maybeTraceToken: Option[TraceToken]
}

trait TraceTokenHolder extends MaybeTraceTokenHolder {
  def traceToken: TraceToken
  override def maybeTraceToken: Option[TraceToken] = Some(traceToken)
}

case class TraceToken(value: String) extends AnyVal {
  def toHeader: TraceTokenHeader = {
    TraceTokenHeader(value)
  }

  override def toString: String = value
}

object TraceToken {
  val MDCKey = "tracetoken"
  def random: TraceToken = TraceToken(UUID.randomUUID().toString)
}
