package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.model.StatusCodes._
import com.lonelyplanet.akka.http.extensions.fixtures.ErrorResponses
import spray.json._

class ExceptionHandlingSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  it should "return correct status codes" in {
    val routing = new Routing

    Get("doesnt-exist") ~> routing.route ~> check {
      val json = responseAs[String].parseJson
      json shouldBe ErrorResponses.defaultErrorResponse
      response.status shouldBe NotFound
    }

    Get("doesnt-exist") ~> addHeader("X-Trace-Token", "test") ~> routing.route ~> check {
      val json = responseAs[String].parseJson
      json shouldBe ErrorResponses.errorResponseWithToken("test")
      response.status shouldBe NotFound
    }

    Get("/exception") ~> routing.route ~> check {
      response.status shouldBe InternalServerError
    }

    Post("/") ~> routing.route ~> check {
      response.status shouldBe MethodNotAllowed
    }

    Get("/resource") ~> routing.route ~> check {
      response.status shouldBe NotFound
    }
  }
}
