package com.lonelyplanet.akka.http.extensions.directives

import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.server.Directives._
import ExpiresDirective._
import akka.http.scaladsl.model.StatusCodes.OK

class ExpiresDirectiveSpec extends FlatSpec with Matchers with ScalatestRouteTest {
  private val expirationDate = DateTime.now

  private val route = path("test") {
    expires(expirationDate) {
      complete {
        "OK"
      }
    }
  }

  "ExpiresDirective" should "set `Expires` header correctly" in {
    Get("/test") ~> route ~> check {
      status shouldBe OK
      responseAs[String] shouldBe "OK"
      header("Expires").get.value shouldBe expirationDate.toRfc1123DateTimeString
    }
  }
}
