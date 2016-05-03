package com.lonelyplanet.akka.http.extensions

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive1, Rejection}
import com.typesafe.config.Config

import scala.collection.immutable.Seq
import scala.util.Try

trait PaginationDirectives {

  private implicit class RichConfigOption[T](value: Option[T]) {
    def ||(defaultValue: T) = value.getOrElse(defaultValue)
  }

  val config: Config

  private def getOrFail[T](key: String)(implicit tag: scala.reflect.ClassTag[T]): T = tag.runtimeClass match {
    case clazz if clazz == classOf[String]  => config.getString(key).asInstanceOf[T]
    case clazz if clazz == classOf[Char]    => config.getString(key).toCharArray()(0).asInstanceOf[T]
    case clazz if clazz == classOf[Boolean] => config.getBoolean(key).asInstanceOf[T]
    case clazz if clazz == classOf[Int]     => config.getInt(key).asInstanceOf[T]
    case clazz if clazz == classOf[Long]    => config.getLong(key).asInstanceOf[T]
    case clazz                              => throw new RuntimeException(s"Invalid property type ${clazz.getSimpleName} for key $key")
  }

  private def get[T](key: String)(implicit tag: scala.reflect.ClassTag[T]): Option[T] = Try(getOrFail(key)).toOption

  private lazy val OffsetParam = get[String]("akka.http.extensions.pagination.offset-param-name") || "offset"
  private lazy val LimitParam = get[String]("akka.http.extensions.pagination.limit-param-name") || "limit"
  private lazy val SortParam = get[String]("akka.http.extensions.pagination.sort-param-name") || "sort"

  private lazy val AscParam = get[String]("akka.http.extensions.pagination.asc-param-name") || "asc"
  private lazy val DescParam = get[String]("akka.http.extensions.pagination.desc-param-name") || "desc"

  private lazy val SortingSeparator = get[String]("akka.http.extensions.pagination.sorting-separator") || ";"
  private lazy val OrderSeparator = get[Char]("akka.http.extensions.pagination.order-separator") || ','

  private lazy val ShouldFallbackToDefaults = get[Boolean]("akka.http.extensions.pagination.defaults.enabled") || false

  private lazy val ShouldAlwaysFallbackToDefaults = get[Boolean]("akka.http.extensions.pagination.defaults.always-fallback") || false

  private lazy val DefaultOffsetParam = get[Int]("akka.http.extensions.pagination.defaults.offset") || 10
  private lazy val DefaultLimitParam = get[Int]("akka.http.extensions.pagination.defaults.limit") || 10

  def pagination: Directive1[Option[PageRequest]] =
    parameterMap.flatMap { params =>
      (params.get(OffsetParam).map(_.toInt), params.get(LimitParam).map(_.toInt)) match {
        case (Some(offset), Some(limit)) => provide(Some(deserializePage(offset, limit, params.get(SortParam))))
        case (Some(offset), None) if ShouldFallbackToDefaults => provide(Some(deserializePage(offset, DefaultLimitParam, params.get(SortParam))))
        case (Some(offset), None) => reject(MalformedPaginationRejection("Missing page limit parameter", None))
        case (None, Some(limit)) if ShouldFallbackToDefaults => provide(Some(deserializePage(DefaultOffsetParam, limit, params.get(SortParam))))
        case (None, Some(limit)) => reject(MalformedPaginationRejection("Missing page offset parameter", None))
        case (_, _) if ShouldAlwaysFallbackToDefaults => provide(Some(deserializePage(DefaultOffsetParam, DefaultLimitParam, params.get(SortParam))))
        case (_, _) => provide(None)
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