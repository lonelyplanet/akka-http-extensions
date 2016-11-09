package com.lonelyplanet.akka.http.extensions.logging

import akka.actor.DiagnosticActorLogging
import akka.event.Logging.MDC
import com.lonelyplanet.akka.http.extensions.tracing.{MaybeTraceTokenHolder, TraceToken}
import org.slf4j.{Logger, LoggerFactory, Marker, MDC => Slf4jMDC}

trait TraceTokenMDCInActorLogging extends DiagnosticActorLogging {
  this: MaybeTraceTokenHolder =>

  override def mdc(currentMessage: Any): MDC = {
    (for {
      traceToken <- maybeTraceToken
    } yield {
      Map(TraceToken.MDCKey -> traceToken.toString)
    }).getOrElse({
      Map.empty
    })
  }
}

trait TraceTokenMDCLogging {
  this: MaybeTraceTokenHolder =>
  def logger: Logger = new TraceTokenMDCLogger(LoggerFactory.getLogger(this.getClass), maybeTraceToken)
}

/**
 * Implementation of the Slf4j Logger interface, that puts a trace token (if one is present)
 * before each logging call to the delegate, and removes it right after.
 *
 * This class shouldn't be used directly. Use 'TraceTokenMDCLogging' instead.
 *
 * @param underlying the delegate logger
 * @param maybeTraceToken a function that will return Some(traceToken), if one should be added to the MDC map
 */
final private class TraceTokenMDCLogger(underlying: Logger, maybeTraceToken: => Option[TraceToken]) extends Logger {

  override def isWarnEnabled: Boolean = underlying.isWarnEnabled
  override def isWarnEnabled(marker: Marker): Boolean = underlying.isWarnEnabled(marker)
  override def isErrorEnabled: Boolean = underlying.isErrorEnabled
  override def isErrorEnabled(marker: Marker): Boolean = underlying.isErrorEnabled(marker)
  override def getName: String = underlying.getName
  override def isInfoEnabled: Boolean = underlying.isInfoEnabled
  override def isInfoEnabled(marker: Marker): Boolean = underlying.isInfoEnabled(marker)
  override def isDebugEnabled: Boolean = underlying.isDebugEnabled
  override def isDebugEnabled(marker: Marker): Boolean = underlying.isDebugEnabled(marker)
  override def isTraceEnabled: Boolean = underlying.isTraceEnabled
  override def isTraceEnabled(marker: Marker): Boolean = underlying.isTraceEnabled(marker)

  override def warn(s: String): Unit = withMDC { underlying.warn(s) }
  override def warn(s: String, o: scala.Any): Unit = withMDC { underlying.warn(s, o) }
  override def warn(s: String, objects: AnyRef*): Unit = withMDC { underlying.warn(s, objects) }
  override def warn(s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.warn(s, o, o1) }
  override def warn(s: String, throwable: Throwable): Unit = withMDC { underlying.warn(s, throwable) }
  override def warn(marker: Marker, s: String): Unit = withMDC { underlying.warn(marker, s) }
  override def warn(marker: Marker, s: String, o: scala.Any): Unit = withMDC { underlying.warn(marker, s, o) }
  override def warn(marker: Marker, s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.warn(marker, s, o, o1) }
  override def warn(marker: Marker, s: String, objects: AnyRef*): Unit = withMDC { underlying.warn(marker, s, objects) }
  override def warn(marker: Marker, s: String, throwable: Throwable): Unit = withMDC { underlying.warn(marker, s, throwable) }

  override def error(s: String): Unit = withMDC { underlying.error(s) }
  override def error(s: String, o: scala.Any): Unit = withMDC { underlying.error(s, o) }
  override def error(s: String, objects: AnyRef*): Unit = withMDC { underlying.error(s, objects) }
  override def error(s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.error(s, o, o1) }
  override def error(s: String, throwable: Throwable): Unit = withMDC { underlying.error(s, throwable) }
  override def error(marker: Marker, s: String): Unit = withMDC { underlying.error(marker, s) }
  override def error(marker: Marker, s: String, o: scala.Any): Unit = withMDC { underlying.error(marker, s, o) }
  override def error(marker: Marker, s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.error(marker, s, o, o1) }
  override def error(marker: Marker, s: String, objects: AnyRef*): Unit = withMDC { underlying.error(marker, s, objects) }
  override def error(marker: Marker, s: String, throwable: Throwable): Unit = withMDC { underlying.error(marker, s, throwable) }

  override def debug(s: String): Unit = withMDC { underlying.debug(s) }
  override def debug(s: String, o: scala.Any): Unit = withMDC { underlying.debug(s, o) }
  override def debug(s: String, objects: AnyRef*): Unit = withMDC { underlying.debug(s, objects) }
  override def debug(s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.debug(s, o, o1) }
  override def debug(s: String, throwable: Throwable): Unit = withMDC { underlying.debug(s, throwable) }
  override def debug(marker: Marker, s: String): Unit = withMDC { underlying.debug(marker, s) }
  override def debug(marker: Marker, s: String, o: scala.Any): Unit = withMDC { underlying.debug(marker, s, o) }
  override def debug(marker: Marker, s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.debug(marker, s, o, o1) }
  override def debug(marker: Marker, s: String, objects: AnyRef*): Unit = withMDC { underlying.debug(marker, s, objects) }
  override def debug(marker: Marker, s: String, throwable: Throwable): Unit = withMDC { underlying.debug(marker, s, throwable) }

  override def trace(s: String): Unit = withMDC { underlying.trace(s) }
  override def trace(s: String, o: scala.Any): Unit = withMDC { underlying.trace(s, o) }
  override def trace(s: String, objects: AnyRef*): Unit = withMDC { underlying.trace(s, objects) }
  override def trace(s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.trace(s, o, o1) }
  override def trace(s: String, throwable: Throwable): Unit = withMDC { underlying.trace(s, throwable) }
  override def trace(marker: Marker, s: String): Unit = withMDC { underlying.trace(marker, s) }
  override def trace(marker: Marker, s: String, o: scala.Any): Unit = withMDC { underlying.trace(marker, s, o) }
  override def trace(marker: Marker, s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.trace(marker, s, o, o1) }
  override def trace(marker: Marker, s: String, objects: AnyRef*): Unit = withMDC { underlying.trace(marker, s, objects) }
  override def trace(marker: Marker, s: String, throwable: Throwable): Unit = withMDC { underlying.trace(marker, s, throwable) }

  override def info(s: String): Unit = withMDC { underlying.info(s) }
  override def info(s: String, o: scala.Any): Unit = withMDC { underlying.info(s, o) }
  override def info(s: String, objects: AnyRef*): Unit = withMDC { underlying.info(s, objects) }
  override def info(s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.info(s, o, o1) }
  override def info(s: String, throwable: Throwable): Unit = withMDC { underlying.info(s, throwable) }
  override def info(marker: Marker, s: String): Unit = withMDC { underlying.info(marker, s) }
  override def info(marker: Marker, s: String, o: scala.Any): Unit = withMDC { underlying.info(marker, s, o) }
  override def info(marker: Marker, s: String, o: scala.Any, o1: scala.Any): Unit = withMDC { underlying.info(marker, s, o, o1) }
  override def info(marker: Marker, s: String, objects: AnyRef*): Unit = withMDC { underlying.info(marker, s, objects) }
  override def info(marker: Marker, s: String, throwable: Throwable): Unit = withMDC { underlying.info(marker, s, throwable) }

  private def withMDC[T](func: => T): T = {
    (for {
      traceToken <- maybeTraceToken
    } yield {
      Slf4jMDC.put(TraceToken.MDCKey, traceToken.toString)
      try {
        func
      } finally {
        Slf4jMDC.remove(TraceToken.MDCKey)
      }
    }).getOrElse({
      func
    })
  }
}