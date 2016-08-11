package com.lonelyplanet.akka.http.extensions.fixtures
import spray.json._

object ErrorResponses {
  val defaultErrorResponse =
    """
      |{
      |  "errors": [
      |    {
      |      "id": "undefined",
      |      "title": "Requested resource not found"
      |    }
      |  ]
      |}
    """.stripMargin.parseJson

  def errorResponseWithToken(token: String) =
    s"""
      |{
      |  "errors": [
      |    {
      |      "id": "$token",
      |      "title": "Requested resource not found"
      |    }
      |  ]
      |}
    """.stripMargin.parseJson
}
