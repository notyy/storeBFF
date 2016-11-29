package com.github.notyy.service

import com.github.notyy.client.ProductClient
import com.github.notyy.domain

import scala.concurrent.Future

case class TempProduct(name: String)

trait SearchingService {
  this: ProductClient =>

  def findByName(name: String): Future[Option[domain.Product]] = queryProductByName(name)

  def findById(id: String): Option[domain.Product] = ???
}

object SearchingService extends SearchingService with ProductClient {
  override protected def host: String = "localhost"

  override protected def port: Int = 8080
}