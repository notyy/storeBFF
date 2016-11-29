package com.github.notyy.client

import akka.actor.ActorSystem
import com.github.notyy.domain.Product
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Uri.{Path, Query}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.scalalogging.slf4j.StrictLogging
import org.json4s.DefaultJsonFormats
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

trait ProductSerializer extends SprayJsonSupport with DefaultJsonProtocol with StrictLogging {
  implicit val ProductFormat = jsonFormat2(Product)
}

trait ProductClient extends ProductSerializer {
  protected def host: String

  protected def port: Int

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def queryProductByName(name: String): Future[Option[Product]] = {
    val theUri = Uri(path = Path("/product/")).withQuery(Query(Map("name" -> "router")))
    logger.info(s"query string is ${theUri.queryString()}")
    logger.info(s"uri is $theUri")
    val source = Source.single(HttpRequest(uri = theUri))
    val flow = Http().outgoingConnection(host, port).mapAsync(1) { rs =>
      logger.info(s"get resonse, status code is ${rs.status}")
      rs.status match {
        case StatusCodes.OK => {
          logger.info(s"response ok, entity is ${rs.entity}")
          Unmarshal(rs.entity).to[Product].map(Some(_))
        }
        case StatusCodes.NotFound => Future(None)
        case _ => Future(None)
      }
    }
    source.via(flow).runWith(Sink.head)
  }
}

