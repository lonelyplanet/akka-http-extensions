Akka-http Extensions
================
[![Join the chat at https://gitter.im/lonelyplanet/akka-http-extensions](https://badges.gitter.im/lonelyplanet/akka-http-extensions.svg)](https://gitter.im/lonelyplanet/akka-http-extensions?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/lonelyplanet/akka-http-extensions.svg?branch=master)](https://travis-ci.org/lonelyplanet/akka-http-extensions)
[![codecov](https://codecov.io/gh/lonelyplanet/akka-http-extensions/branch/master/graph/badge.svg)](https://codecov.io/gh/lonelyplanet/akka-http-extensions)
[ ![Download](https://api.bintray.com/packages/lonelyplanet/maven/akka-http-extensions/images/download.svg) ](https://bintray.com/lonelyplanet/maven/akka-http-extensions/_latestVersion)

akka-http-extensions its a simple library with useful tools for develop web apps with Akka-http.

This is an updated project that uses Akka-http instead of Spray: https://github.com/Jarlakxen/spray-extensions


### Downloading

    "com.lonelyplanet" %% "akka-http-extensions" % "0.4.6"

### Changelog

0.4
- Fallback to default limit and offset parameters in case they are missing

0.3
- Pagination support


# Features

## Pagination Support

The trait PaginationDirectives offers some helpers for handling pagination:

```
path("filter-test") {
  withOptionalPagination { page =>
    complete {
      page match {
        case Some(page) => ... // A page was requested
        case None => ... // No page was requested
      }
    }
  }
}
```

Alternatively you can use:
```
path("filter-test") {
  withPagination { page =>
    complete {
      // page is always set
    }
  }
}
```

The page object has this format

```
sealed trait Order

object Order {
  case object Asc extends Order
  case object Desc extends Order
}

case class PageRequest(index: Int, size: Int, sort: Map[String, Order])
```

This is an example of url: `/filter-test?page=1&size=10` or `/filter-test?page=1&size=10&sort=name,asc;age,desc`

The name of the parameters can be configured through [Typesafe Config](https://github.com/typesafehub/config):

```

akka.http {
    extensions {
        pagination{
            index-param-name = "page"
            size-param-name  = "size"
            sort-param-name  = "sort"
            asc-param-name   = "asc"
            desc-param-name  = "desc"
            sorting-separator = ";"
            order-separator  = ","
            defaults {
                enabled = true
                always-fallback = true
                offset = 10
                limit = 10
            }
        }
    }
}

```

### Publishing

We use `bintray-sbt` plugin for publishing artifacts, to publish newer version of the library run:
```
sbt publish
```

That's all what is required
