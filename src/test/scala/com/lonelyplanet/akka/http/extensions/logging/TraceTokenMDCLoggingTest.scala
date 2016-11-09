package com.lonelyplanet.akka.http.extensions.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.{Level, Logger, LoggerContext}
import ch.qos.logback.core.AppenderBase
import com.lonelyplanet.akka.http.extensions.tracing.{MaybeTraceTokenHolder, TraceToken}
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory

import scala.util.Random


class TraceTokenMDCLoggingSpec extends FlatSpec with Matchers {
  it should "log trace token if one is present" in {
    withInMemoryAppender { appender =>
      val traceToken = TraceToken.random
      val loggingTester = new LoggingTester(Some(traceToken))
      val message = randomMessage

      loggingTester.doLog(message)

      appender.output should not be empty
      appender.output.lines.foreach({ line =>
        line.contains(message) shouldBe true
        line.contains(traceToken.toString) shouldBe true
      })
    }
  }

  private def withInMemoryAppender(f: (InMemoryLoggingAppender) => Unit) = {
    val loggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    val appender = new InMemoryLoggingAppender
    appender.setContext(loggerContext)

    val logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger]
    logger.setLevel(Level.ALL)
    logger.detachAndStopAllAppenders()

    logger.addAppender(appender)
    appender.start()
    f(appender)
    logger.detachAppender(appender)
    appender.stop()
  }

  private def randomMessage = Random.alphanumeric.take(20).mkString("")
}

private class InMemoryLoggingAppender extends AppenderBase[ILoggingEvent] {
  private val builder = new StringBuilder

  override def append(event: ILoggingEvent): Unit = {
    builder.append(event.getMessage)
    builder.append(" ")
    if (event.getMDCPropertyMap.containsKey(TraceToken.MDCKey)) {
      builder.append(event.getMDCPropertyMap.get(TraceToken.MDCKey))
    }
    builder.append("\n")
  }

  def output: String = builder.toString()
  def clear(): Unit = builder.clear()
}

private class LoggingTester(maybeTraceTokenFunc: => Option[TraceToken]) extends TraceTokenMDCLogging with MaybeTraceTokenHolder {
  override def maybeTraceToken: Option[TraceToken] = maybeTraceTokenFunc
  def doLog(message: String): Unit = {
    logger.trace(message)
    logger.debug(message)
    logger.info(message)
    logger.warn(message)
    logger.error(message)
  }
}
