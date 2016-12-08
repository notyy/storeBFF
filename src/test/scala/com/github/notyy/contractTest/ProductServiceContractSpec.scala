package com.github.notyy.contractTest

import com.github.notyy.client.ProductClient
import com.github.notyy.domain.Product
import org.json4s.DefaultFormats
import org.json4s.native.Serialization._
import org.scalatest.{FunSpec, ShouldMatchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class ProductServiceContractSpec extends FunSpec with ShouldMatchers {

  import com.itv.scalapact.ScalaPactForger._

  implicit val formats = DefaultFormats

  describe("ProductService provider") {
    describe("should find product by name") {
      it("should be able to find product by name if exist") {
        forgePact
          .between("storeBFF")
          .and("storeProduct")
          .addInteraction(
            interaction
              .description("find product if exist")
              .given("product named `router` exists")
              .uponReceiving("/product/?name=router")
              .willRespondWith(
                status = 200,
                headers = Map("Content-Type" -> "application/json"),
                body = write(Product("1", "router")))
          )
          .runConsumerTest { mockConfig =>
            val productClient = new ProductClient {
              protected override def host: String = mockConfig.host

              protected override def port: Int = mockConfig.port
            }

            val optProduct = Await.result(productClient.queryProductByName("router"), 1 second)
            optProduct should not be empty
            optProduct.get.id should not be empty
            optProduct.get.name shouldBe "router"
          }
      }
      it("returns 404 if product not found") {
        forgePact
          .between("storeBFF")
          .and("storeProduct")
          .addInteraction(
            interaction
              .description("returns 404 if product not found")
              .given("product named `not_exist` doesn't exists")
              .uponReceiving("/product/?name=not_exist")
              .willRespondWith(
                status = 404
              )
          )
          .runConsumerTest { mockConfig =>
            val productClient = new ProductClient {
              protected override def host: String = mockConfig.host

              protected override def port: Int = mockConfig.port
            }

            val optProduct = Await.result(productClient.queryProductByName("not_exist"), 1 second)
            optProduct shouldBe empty
          }
      }
    }
  }
}
