package org.dyne.danielsan.openblockchain.http.controllers

import org.scalatra.ScalatraServlet
import org.scalatra.swagger.{ApiInfo, JacksonSwaggerBase, Swagger}

/**
  * Created by dan_mi_sun on 22/05/2016.
  */
class ApiDocsController(implicit val swagger: Swagger) extends ScalatraServlet with JacksonSwaggerBase

object OpenBlockchainApiInfo extends ApiInfo(
  "The OpenBlockchain API",
  "Docs for the OpenBlockchain API",
  "http://openblockchain.info",
  "daniel.san@dyne.org",
  "MIT",
  "http://opensource.org/licences/MIT")

class OpenBlockchainSwagger extends Swagger(Swagger.SpecVersion, "1.0.0", OpenBlockchainApiInfo)
