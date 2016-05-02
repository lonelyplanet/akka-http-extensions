package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.server.{Directive1, Rejection}
import com.lonelyplanet.utils.Config._
import akka.http.scaladsl.server.Directives._
import scala.collection.immutable.Seq

trait PaginationDirectives {

  private val OffsetParam = Config.get[String]("akka.http.extensions.pagination.offset-param-name") || "offset"
  private val LimitParam = Config.get[String]("akka.http.extensions.pagination.limit-param-name") || "limit"
  private val SortParam = Config.get[String]("akka.http.extensions.pagination.sort-param-name") || "sort"

  private val AscParam = Config.get[String]("akka.http.extensions.pagination.asc-param-name") || "asc"
  private val DescParam = Config.get[String]("akka.http.extensions.pagination.desc-param-name") || "desc"

  private val SortingSeparator = Config.get[String]("akka.http.extensions.pagination.sorting-separator") || ";"
  private val OrderSeparator = Config.get[Char]("akka.http.extensions.pagination.order-separator") || ','

  def pagination: Directive1[Option[PageRequest]] =
    parameterMap.flatMap { params =>
      (params.get(OffsetParam).map(_.toInt), params.get(LimitParam).map(_.toInt)) match {
        case (Some(offset), Some(limit)) => provide(Some(deserializePage(offset, limit, params.get(SortParam))))
        case (Some(offset), None)        => reject(MalformedPaginationRejection("Missing page limit parameter", None))
        case (None, Some(limit))         => reject(MalformedPaginationRejection("Missing page offset parameter", None))
        case (_, _)                      => provide(None)
      }
    }

  private def deserializePage(offset: Int, limit: Int, sorting: Option[String]) = {

    val sortingParam = sorting.map(_.split(SortingSeparator).map(_.span(_ != OrderSeparator)).collect {
      case (field, sort) if sort == ',' + AscParam  => (field, Order.Asc)
      case (field, sort) if sort == ',' + DescParam => (field, Order.Desc)
    }.toMap)

    PageRequest(offset, limit, sortingParam.getOrElse(Map.empty))
  }

  case class MalformedPaginationRejection(errorMsg: String, cause: Option[Throwable] = None) extends Rejection

}

sealed trait Order

object Order {
  case object Asc extends Order
  case object Desc extends Order
}

case class PageRequest(offset: Int, limit: Int, sort: Map[String, Order])

case class PageResponse[T](elements: Seq[T], totalElements: Int)