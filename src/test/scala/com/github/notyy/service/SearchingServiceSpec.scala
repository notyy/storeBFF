package com.github.notyy.service

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.scalatest.{FunSpec, ShouldMatchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class SearchingServiceSpec extends FunSpec with ShouldMatchers with StrictLogging {

  describe("SearchingService"){
    ignore("can find product by name"){
      val optProduct = Await.result(SearchingService.findByName("router"),1 second)
      optProduct should not be empty
      optProduct.get.id should not be empty
      optProduct.get.name shouldBe "router"
    }
  }
}
