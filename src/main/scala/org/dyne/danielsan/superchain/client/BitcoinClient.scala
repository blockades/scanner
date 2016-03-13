package org.dyne.danielsan.superchain.client

import org.dyne.danielsan.superchain.data.models.Block
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write

import scala.language.postfixOps
import scalaj.http.{Base64, Http}
import sys.process._

/**
  * Created by dan_mi_sun on 10/03/2016.
  */
class BitcoinClient {

  implicit val formats = DefaultFormats

  val baseUrl = "http://127.0.0.1:8332"

  def getHashForId(id: Int): String = {
    val request = BtcRequest("getblockhash", List(id))
    val json = write(request)
    Http(baseUrl).postData(json)
      .header("content-type", "application/json")
      .header("Authorization", auth)
      .asString
      .body
  }


  def getBlockForHash(hash: String): String = {
    val request = BtcRequest("getblock", List(hash))
    val json = write(request)
    Http(baseUrl).postData(json)
      .header("content-type", "application/json")
      .header("Authorization", auth)
      .asString
      .body
  }

  def getBlockForId(id: Int): Block = {
    val hash = getHashForId(id)
    val blockString = getBlockForHash(hash)
    val block = parse(blockString).extract[Block]
    println("b =" + block)
    block
  }


  def getBlockChainFromId(id: Int): Block = {
    val block = getBlockForId(1)
    val hash = block.hash
    val nextId = id + 1
    println("Found: " + block)
    getBlockChainFromId(nextId)
  }

  private

  def auth = {
    "Basic " + Base64.encodeString("dave:suckme")
  }
}

case class BtcRequest(method: String, params: List[Any])

