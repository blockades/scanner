package org.dyne.danielsan.superchain.data.bitcoinrpc

import argonaut._, argonaut.Argonaut._
import org.dyne.danielsan.superchain.data.models.BlockHashImplicitConversion.BlockHash
import org.dyne.danielsan.superchain.data.models.BlockImplicitConversion.Block
import org.dyne.danielsan.superchain.data.models.TransactionImplicitConversion.Transaction
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
  * Created by dan_mi_sun on 23/02/2016.
  */

case class JSONRPCRequest(id: String, method: String, params: Json)

object JSONRPCRequest {
  implicit def JSONRPCRequestJson =
    casecodec3(JSONRPCRequest.apply, JSONRPCRequest.unapply)("id", "method", "params")
}

case class JSONRPCResponse(result: Json, error: Option[String], id: String)

object JSONRPCResponse {
  implicit def JSONRPCResponseJson =
    casecodec3(JSONRPCResponse.apply, JSONRPCResponse.unapply)("result", "error", "id")
}

class JSONRPC(rpcURL: String, username: String, password: String) {

  import dispatch._, Defaults._

  def doRequest[A](req: JSONRPCRequest, responseTransformer: (Json => A)) = {
    println("DO REQUEST: " + req.asJson.toString())
    val dispatchReq = url(rpcURL).addHeader("Content-Type", "application/x-www-form-urlencoded").POST.as(username,
      password) << req.asJson.toString()
    val resp = Http(dispatchReq OK as.String).either.right.map(_.decodeOption[JSONRPCResponse])

    resp() match {
      case Right(Some(JSONRPCResponse(r, None, _))) => Right(responseTransformer(r))
      case Right(Some(JSONRPCResponse(_, Some(error), _))) => Left(error)
      case Right(_) => Left("No response case class found")
      case Left(t) => Left(t.getMessage)
    }
  }
}

object BitcoinRPC {
  val jsonToString: (Json => String) = a => a.string.get.toString

  val emptyjArray = jArray(List())

  def jStringInjArray(s: String) = jArray(List(jString(s)))

  def getinfo(implicit jsonrpc: JSONRPC) = {
    jsonrpc.doRequest(JSONRPCRequest("t0", "getinfo", emptyjArray), identity[Json])
  }


  def getrawtransaction(tid: String, verbose: Boolean)(implicit jsonrpc: JSONRPC) = {
    val json = jArray(List(jString(tid), jNumber(if (verbose) 1 else 0)))
    jsonrpc.doRequest(JSONRPCRequest("t0", "getrawtransaction", json), identity[Json])
  }

  def getblockhash(tid: Int)(implicit jsonrpc: JSONRPC) = {
    val json = jArray(List(jNumber(tid)))
    jsonrpc.doRequest(JSONRPCRequest("t0", "getblockhash", json), identity[Json])
  }

  def getblock(hash: String)(implicit jsonrpc: JSONRPC) = {
    val json = jArray(List(jString(hash)))
    jsonrpc.doRequest(JSONRPCRequest("t0", "getblock", json), identity[Json])
  }
}

/*
All of the following below here is just to dry run commands to see if they work etc
Not proposing this should be left in here.
At the moment the way that I have been doing it means that each of the commands are imperative and chained one after
the other which doesn't intuitively feel like the best way to do this.
Still need to explore what is a good approach

Inputs have also been hardcoded as a first step.
*/

object JSONRPCMain extends App {
  implicit val formats = DefaultFormats

  val btcurl = "http://127.0.0.1:8332"

  implicit val jsonrpc = new JSONRPC(btcurl, "dave", "suckme")

  val resp = BitcoinRPC.getblockhash(1)

  println("Resp: " + resp)
  resp match {
    case Left(error) => println(s"there was an error: $error")
    case Right(json) => val getraw = json.toString()
      println("getraw: " + getraw)

      //val t = parse(getraw).extract[Transaction]
      val t = getraw
      println("T is:" + t)

      val blockResp = BitcoinRPC.getblock("00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048")

      println("blockResp: " + blockResp)

      blockResp match {
        case Left(error) => println(s"there was an error: $error")
        case Right(blockJson) => val blockRaw = s""" $blockJson """

          println("blockRaw: " + blockRaw)

          val b = parse(blockRaw).extract[Block]
          println("B is: " + b)

          println("B(tx) is:" + b)


        /*
      val transResp = BitcoinRPC.getrawtransaction("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098",
        verbose = true)

        println("transResp: " + transResp)

        */
      }

  }
}