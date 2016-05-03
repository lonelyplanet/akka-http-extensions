package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration.FiniteDuration

class PaginationSpec extends FlatSpec with PaginationDirectives with Matchers with ScalatestRouteTest {
  implicit val routeTestTimeout: RouteTestTimeout = RouteTestTimeout(FiniteDuration(10, "s"))
  val config = testConfig
  def route =
    path("filter-test") {
      pagination { page =>
        complete {
          page match {
            case Some(p) => p.toString
            case None    => "NoPage"
          }
        }
      }
    }
}
