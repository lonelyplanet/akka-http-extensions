package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.model.StatusCodes

class PaginationDirectivesSpec extends PaginationSpec {
  "Pagination" should "not have page if no page is requested" in {
    Get("/filter-test") ~> paginationRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual "NoPage"
    }

    Get("/filter-test") ~> paginationOrDefaultsRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual PageRequest(0, 10, Map.empty).toString
    }
  }

  it should "not have page if page requested have incomplete parameters" in {
    Get("/filter-test?offset=1") ~> paginationRoute ~> check {
      rejection shouldBe a[MalformedPaginationRejection]
    }

    Get("/filter-test?offset=1") ~> paginationOrDefaultsRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual PageRequest(1, 10, Map.empty).toString
    }
  }

  it should "return the page object that was requested" in {
    Get("/filter-test?offset=1&limit=10&sort=name,asc;age,desc") ~> paginationRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual PageRequest(1, 10, Map("name" -> Order.Asc, "age" -> Order.Desc)).toString
    }

    Get("/filter-test?offset=1&limit=10&sort=name,asc;age,desc") ~> paginationOrDefaultsRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual PageRequest(1, 10, Map("name" -> Order.Asc, "age" -> Order.Desc)).toString
    }
  }
}