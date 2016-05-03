package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.server.Directives._

import scala.concurrent.duration.FiniteDuration

class PaginationDirectivesSpec extends FlatSpec with PaginationDirectives with Matchers with ScalatestRouteTest {

  implicit val routeTestTimeout: RouteTestTimeout = RouteTestTimeout(FiniteDuration(10, "s"))
  val config = testConfig

override def testConfigSource = "akka.http.extensions.pagination.defaults.enabled = true"
/*  override def testConfigSource =
    """
      |akka{
      |http {
      |    extensions {
      |        rest {
      |            pagination{
      |                index-param-name = "page"
      |                size-param-name  = "size"
      |                sort-param-name  = "sort"
      |                asc-param-name   = "asc"
      |                desc-param-name  = "desc"
      |                sorting-separator = ";"
      |                order-separator  = ","
      |                defaults {
      |                    enabled = true
      |                    offset = 10
      |                    limit = 10
      |                }
      |            }
      |        }
      |    }
      |    }
      |}
    """.stripMargin*/



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



  "Pagination" should "not have page if no page is requested" in {

    Get("/filter-test") ~> route ~> check {
      status === StatusCodes.OK
      responseAs[String] === "NoPage"
    }
  }

  it should "not have page if page requested have incomplete parameters" in {
    Get("/filter-test?offset=1") ~> route ~> check {
      rejection shouldBe a[MalformedPaginationRejection]
    }
  }

  it should "return the page object that was requested" in {
    Get("/filter-test?offset=1&limit=10&sort=name,asc;age,desc") ~> route ~> check {
      status === StatusCodes.OK
      responseAs[String] === PageRequest(1, 10, Map("name" -> Order.Asc, "age" -> Order.Desc)).toString
    }
  }
}