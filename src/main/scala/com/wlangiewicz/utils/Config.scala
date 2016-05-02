package com.wlangiewicz.utils

import com.typesafe.config.ConfigFactory
import scala.util._

object Config {

  implicit class RichConfigOption[T](value: Option[T]) {
    def ||(defaultValue: T) = value.getOrElse(defaultValue)
  }

  object Config {
    val content = ConfigFactory.load()

    def getOrFail[T](key: String)(implicit tag: scala.reflect.ClassTag[T]): T = tag.runtimeClass match {
      case clazz if clazz == classOf[String] => content.getString(key).asInstanceOf[T]
      case clazz if clazz == classOf[Char] => content.getString(key).toCharArray()(0).asInstanceOf[T]
      case clazz if clazz == classOf[Boolean] => content.getBoolean(key).asInstanceOf[T]
      case clazz if clazz == classOf[Int] => content.getInt(key).asInstanceOf[T]
      case clazz if clazz == classOf[Long] => content.getLong(key).asInstanceOf[T]
      case clazz => throw new RuntimeException(s"Invalid property type ${clazz.getSimpleName} for key $key")
    }

    def get[T](key: String)(implicit tag: scala.reflect.ClassTag[T]): Option[T] = Try(getOrFail(key)).toOption
  }
}