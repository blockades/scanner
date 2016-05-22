package org.dyne.danielsan.openblockchain.http.controllers.api

import org.dyne.danielsan.openblockchain.OpenBlockchainStack
import org.dyne.danielsan.openblockchain.data.database.ChainDatabase
import org.dyne.danielsan.openblockchain.http.controllers.api.swaggerdocs.TransactionsControllerDocs
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.FutureSupport
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.Swagger

import scala.concurrent.Await
import scala.concurrent.duration._


/**
  * Created by dan_mi_sun on 22/05/2016.
  */
class TransactionsController(implicit val swagger: Swagger) extends OpenBlockchainStack with FutureSupport with JacksonJsonSupport with TransactionsControllerDocs {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  val executor = scala.concurrent.ExecutionContext.global

  before() {
    contentType = formats("json")
  }

  get("/", operation(getTransactions)) {
    ChainDatabase.listAllTransactions
  }

  get("/:id", operation(getTransaction)) {
    val id = params("id")
    Await.result(ChainDatabase.getTransactionByTxid(id), 3.seconds) match {
      case Some(block) => block
      case None => halt(404, "")
    }

  }


}
