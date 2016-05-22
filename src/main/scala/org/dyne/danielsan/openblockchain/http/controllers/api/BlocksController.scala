package org.dyne.danielsan.openblockchain.http.controllers.api

import org.dyne.danielsan.openblockchain.data.database.ChainDatabase
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{FutureSupport, ScalatraServlet}

import scala.concurrent.Await
import scala.concurrent.duration._


/**
  * Created by dan_mi_sun on 20/05/2016.
  */
class BlocksController extends ScalatraServlet with FutureSupport with JacksonJsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  val executor = scala.concurrent.ExecutionContext.global

  before() {
    contentType = formats("json")
  }

  get("/") {
    ChainDatabase.listAllBlocks
  }

  get("/:id") {
    val id = params("id")
    Await.result(ChainDatabase.getBlockByHash(id), 3.seconds) match {
      case Some(block) => block
      case None => halt(404, "")
    }

  }

}


