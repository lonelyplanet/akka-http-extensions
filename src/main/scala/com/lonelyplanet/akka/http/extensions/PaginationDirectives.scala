package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.server.{Directive1, Rejection}
import com.lonelyplanet.utils.Config._
import akka.http.scaladsl.server.Directives._
import scala.collection.immutable.Seq

trait PaginationDirectives {

  private val IndexParam = Config.get[String]("akka.http.extensions.pagination.index-param-name") || "page"
  private val SizeParam = Config.get[String]("akka.http.extensions.pagination.size-param-name") || "size"
  private val SortParam = Config.get[String]("akka.http.extensions.pagination.sort-param-name") || "sort"

  private val AscParam = Config.get[String]("akka.http.extensions.pagination.asc-param-name") || "asc"
  private val DescParam = Config.get[String]("akka.http.extensions.pagination.desc-param-name") || "desc"

  private val SortingSeparator = Config.get[String]("akka.http.extensions.pagination.sorting-separator") || ";"
  private val OrderSeparator = Config.get[Char]("akka.http.extensions.pagination.order-separator") || ','

  def pagination: Directive1[Option[PageRequest]] =
    parameterMap.flatMap { params =>
      (params.get(IndexParam).map(_.toInt), params.get(SizeParam).map(_.toInt)) match {
        case (Some(index), Some(size)) => provide(Some(deserializePage(index, size, params.get(SortParam))))
        case (Some(index), None)       => reject(MalformedPaginationRejection("Missing page size parameter", None))
        case (None, Some(size))        => reject(MalformedPaginationRejection("Missing page index parameter", None))
        case (_, _)                    => provide(None)
      }
    }

  private def deserializePage(index: Int, size: Int, sorting: Option[String]) = {

    val sortingParam = sorting.map(_.split(SortingSeparator).map(_.span(_ != OrderSeparator)).collect {
      case (field, sort) if sort == ',' + AscParam  => (field, Order.Asc)
      case (field, sort) if sort == ',' + DescParam => (field, Order.Desc)
    }.toMap)

    PageRequest(index, size, sortingParam.getOrElse(Map.empty))
  }

  case class MalformedPaginationRejection(errorMsg: String, cause: Option[Throwable] = None) extends Rejection

}

sealed trait Order

object Order {
  case object Asc extends Order
  case object Desc extends Order
}

case class PageRequest(index: Int, size: Int, sort: Map[String, Order])

case class PageResponse[T](elements: Seq[T], totalElements: Int)