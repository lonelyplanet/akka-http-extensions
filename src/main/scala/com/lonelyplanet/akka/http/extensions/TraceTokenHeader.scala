package com.lonelyplanet.akka.http.extensions

import java.util.UUID

import akka.http.scaladsl.model.headers.{ModeledCustomHeader, ModeledCustomHeaderCompanion}

import scala.util.Try

final class TraceTokenHeader(token: String) extends ModeledCustomHeader[TraceTokenHeader] {
  def renderInRequests = false
  def renderInResponses = false
  override val companion = TraceTokenHeader
  override def value: String = token
}

object TraceTokenHeader extends ModeledCustomHeaderCompanion[TraceTokenHeader] {
  val MDCKey = "tracetoken"
  def random = Try(new TraceTokenHeader(UUID.randomUUID().toString))
  def renderInRequests = false
  def renderInResponses = false
  override val name = "X-Trace-Token"
  override def parse(value: String) = Try(new TraceTokenHeader(value))
}

