package org.dyne.danielsan.superchain.client

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
  val hashQuery = "/getblockhash/"
  val blockQuery = "/getblock/"
  //  val rawTransactionQuery = "//"

  def getHashForId(id: Int): String = {
    //    val hash = (s"bitcoin-cli getblockhash $id" !!).trim
    val hash = hashForIdRequest(id)
    println(hash)
    hash
  }


  def getBlockForHash(hash: String): String = {
    val block = (s"bitcoin-cli getblock $hash" !!).trim
    println("b =" + block)
    block

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
    println(s"Block $hash found")
    val nextId = id + 1
    getBlockChainFromId(nextId)
  }

  private

  def hashForIdRequest(id: Int): String = {
    val request = BtcRequest("getblockhash", List(id))
    val json = write(request)
    val response = Http(baseUrl).postData(json)
      .header("content-type", "application/json")
      .header("Authorization", auth)
      .asString
      .body
    println("RESPONSE: " + response)
    response
  }

  def auth = {
    "Basic " + Base64.encodeString("dave:suckme")
  }
}

case class BtcRequest(method: String, params: List[Int])

case class Block(hash: String,
                 confirmations: Int,
                 size: Int,
                 height: Int,
                 version: Int,
                 merkleroot: String,
                 tx: List[String],
                 time: Long,
                 nonce: Long,
                 bits: String,
                 difficulty: Float,
                 chainwork: String,
                 previousblockhash: String,
                 nextblockhash: String)