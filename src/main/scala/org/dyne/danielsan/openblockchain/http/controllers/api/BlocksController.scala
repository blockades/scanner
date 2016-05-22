package org.dyne.danielsan.openblockchain.http.controllers.api

import org.dyne.danielsan.openblockchain.data.database.ChainDatabase
import org.dyne.danielsan.openblockchain.data.entity.Block

import org.json4s.{Formats, DefaultFormats}
import org.scalatra.{NoContent, FutureSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport


import scala.concurrent.Await
import scala.concurrent.duration._


/**
  * Created by dan_mi_sun on 20/05/2016.
  */
class BlocksController extends ScalatraServlet  with FutureSupport with JacksonJsonSupport {

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

/*
import com.joonski.whiskysteak.data.cassandra.repositories.BoatsRepository
import com.joonski.whiskysteak.data.models.Boat
import com.joonski.whiskysteak.http.controllers.api.swaggerdocs.BoatsControllerDocs
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.swagger.{Swagger, SwaggerEngine}
import org.scalatra.{NoContent, FutureSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by Nargess on 18/10/15.
  */
class BoatsController (implicit val swagger: Swagger) extends ScalatraServlet with FutureSupport with JacksonJsonSupport with BoatsControllerDocs {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  val executor = scala.concurrent.ExecutionContext.global

  before() {
    contentType = formats("json")
  }

  get("/", operation(getBoats)) {
    BoatsRepository.listAll
  }

  get("/:id", operation(getBoat)) {
    val id = params("id")
    Await.result(BoatsRepository.getById(id), 3.seconds) match {
      case Some(boat) => boat
      case None => halt(404, "")
    }
  }

  post("/", operation(createBoat)) {
    val name = params("name")
    val model = params("model")
    val length = params("length").toInt
    val boat = Boat("", name, model, length)
    BoatsRepository.insertNewRecord(boat)
  }

  delete("/:id", operation(destroyBoat)) {
    val id = params("id")
    BoatsRepository.destroy(id)
    NoContent(Map())
  }

  put("/:id", operation(updateBoat)) {
    val id = params("id")
    val name = params("name")
    val model = params("model")
    val length = params("length").toInt
    val boat = Boat(id, name, model, length)
    BoatsRepository.updateBoat(boat)

  }
  }
**/


