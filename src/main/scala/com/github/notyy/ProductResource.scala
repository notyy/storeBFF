package com.github.notyy

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.github.notyy.domain.Product
import com.github.notyy.service.{SearchingService, TempProduct}
import com.typesafe.scalalogging.slf4j.StrictLogging
import spray.json.DefaultJsonProtocol


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val productFormat = jsonFormat2(Product)
  implicit val tempProductFormat = jsonFormat1(TempProduct)
}

object ProductResource extends Directives with JsonSupport with StrictLogging {
  val route: Route =
    path("product") {
      get {
        complete(List[Product]())
      }
    } ~
      pathPrefix("product" / Segment) { productId =>
        get {
          complete(Product("1234", "roboto")) // will render as JSON
        }
      } ~
      pathPrefix("search") {
        get {
          parameters('name) { name =>
            val optProduct = SearchingService.findByName(name)
            logger.info(s"query product by name: $name")
            onSuccess(optProduct){
              case Some(product) => complete(product)
              case None =>               complete(StatusCodes.NotFound)
            }
            }
          }
        }
//          post {
//            entity(as[TempProduct]) { tempProduct => // will unmarshal JSON to Order
//              val optProduct = SearchingService.create(tempProduct)
//              if (optProduct.isEmpty) {
//                complete(StatusCodes.BadRequest)
//              } else {
//                complete(StatusCodes.Created, s"Product created with id: ${optProduct.get.id}")
//              }
//            }
//          }


}
