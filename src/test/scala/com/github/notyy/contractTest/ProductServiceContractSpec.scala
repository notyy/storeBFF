package com.github.notyy.contractTest

import com.github.notyy.client.ProductClient
import com.github.notyy.domain.Product
import org.json4s.DefaultFormats
import org.json4s.native.Serialization._
import org.scalatest.{FunSpec, ShouldMatchers}

class ProductServiceContractSpec extends FunSpec with ShouldMatchers {
  import com.itv.scalapact.ScalaPactForger._

  implicit val formats = DefaultFormats
  import scala.concurrent.ExecutionContext.Implicits.global

  describe("ProductService provider"){
    it("should be able to find product by name"){
      forgePact
        .between("storeBFF")
        .and("storeProduct")
        .addInteraction(
          interaction
            .description("find product by name")
            .given("product named `router` exists")
            .uponReceiving("/product/?name=router")
            .willRespondWith(200, write(Product("1","router")))
        )
        .runConsumerTest { mockConfig =>
          val productClient = new ProductClient {
            protected override def host = mockConfig.host

            protected override def port = mockConfig.port
          }

          val futureProduct =  productClient.queryProductByName("router")
          futureProduct.onSuccess{
            case Some(product) => {
              product.get.id should not be empty
              product.get.name shouldBe "router"
            }
            case None => fail("product named 'router' not found")
          }
        }
    }
  }

}
