package com.github.notyy.service

import com.github.notyy.client.ProductClient
import com.typesafe.scalalogging.slf4j.StrictLogging
import org.scalatest.{FunSpec, ShouldMatchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class SearchingServiceSpec extends FunSpec with ShouldMatchers with StrictLogging {

  val searchingService = new SearchingService with ProductClient{
    override protected def host: String = "localhost"
    override protected def port: Int = 1234
  }

  describe("SearchingService"){
    it("can find product by name"){
      val optProduct = Await.result(searchingService.findByName("router"),1 second)
      optProduct should not be empty
      optProduct.get.id should not be empty
      optProduct.get.name shouldBe "router"
    }
  }
}
