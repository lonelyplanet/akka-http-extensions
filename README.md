Akka-http Extensions
================

akka-http-extensions its a simple library with useful tools for develop web apps with Akka-http.

This is an updated project that uses Akka-http instead of Spray: https://github.com/Jarlakxen/spray-extensions


### Downloading

  [ ![Download](https://api.bintray.com/packages/lonelyplanet/maven/akka-http-extensions/images/download.svg) ](https://bintray.com/lonelyplanet/maven/akka-http-extensions/_latestVersion)

    "com.lonelyplanet" %% "akka-http-extensions" % "0.3"

### Changelog

0.3
- Pagination support


# Features

## Pagination Support

The trait PaginationDirectives offers some helpers for handling pagination:

```
path("filter-test") {
  pagination { page =>
    complete {
      page match {
        case Some(page) => ... // A page was requested
        case None => ... // No page was requested
      }
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
        rest{
            pagination{
                index-param-name = "page"
                size-param-name  = "size"
                sort-param-name  = "sort"
                asc-param-name   = "asc"
                desc-param-name  = "desc"
                sorting-separator = ";"
                order-separator  = ","
                totalelements-header-name = "total_elements"
            }
        }
    }
}

```

When you replay a page, you sometime needs to send te total number of elements. This is useful for the client for knowing how many pages they are. For this scenario there is:

```

path("filter-test") {
  pagination { page =>
    complete {
      page match {
        case Some(page) => {
            val response = PageResponse(List("test"), 10)
            completePage(response)
        }
        case None => ... // No page was requested
      }
    }
  }
}

```

The total number of elements is sent in the header `total_elements`
