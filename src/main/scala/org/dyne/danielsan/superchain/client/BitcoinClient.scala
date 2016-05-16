package org.dyne.danielsan.superchain.client

import org.dyne.danielsan.superchain.data.entity.{Block, Transaction}
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.json4s.{DefaultFormats, _}

import scala.language.postfixOps
import scalaj.http.{Base64, Http}

/*
Known limitation is that there is alot of repition (i.e. not DRY) - within the methods
themselves - is it possibleto abstract these out into another method so that we can DRY
it up
 */

/**
  * Created by dan_mi_sun on 10/03/2016.
  */
class BitcoinClient {

  implicit val formats = DefaultFormats

  val baseUrl = "http://127.0.0.1:8332"

  def decodeRawTransaction(id: Int): Transaction = {
    val rawTx = getRawTransaction(id)
    val request = BtcRequest("decoderawtransaction", List(rawTx))
    val json = write(request)
    val resp = Http(baseUrl).postData(json)
      .header("content-type", "application/json")
      .header("Authorization", auth)
      .asString
      .body
    (parse(resp) \ "result").extract[Transaction]
  }

  def getRawTransaction(id: Int): String = {
    val txId = extractTransactionIds(id)
    val request = BtcRequest("getrawtransaction", List(txId))
    val json = write(request)
    val resp = Http(baseUrl).postData(json)
      .header("content-type", "application/json")
      .header("Authorization", auth)
      .asString
      .body
    (parse(resp) \ "result").extract[String]
  }

  // Known limitation is that this is only going to take the head of the list
  // Many blocks have many TransactionsIds within a block
  def extractTransactionIds(id: Int): String = {
    val transactionList = getTransactionIdsFromWithinBlock(id)
    transactionList.head
  }

  def getTransactionIdsFromWithinBlock(id: Int): List[String] = {
    val block = getBlockForId(id)
    block.tx
  }

  def getTransactionCountFromWithinBlock(id: Int) : Int = {
    val block = getBlockForId(id)
    block.tx.length
  }

  def getBlockForId(id: Int): Block = {
    val hash: String = getBlockHashForId(id)
    val blockString = getBlockForHash(hash)
    val json = parse(blockString) \ "result"
    json.extract[Block]
  }

  def getBlockHashForId(id: Int): String = {
    val request = BtcRequest("getblockhash", List(id))
    val json = write(request)
    val resp = Http(baseUrl).postData(json)
      .header("content-type", "application/json")
      .header("Authorization", auth)
      .asString
      .body
    (parse(resp) \ "result").extract[String]
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

  private

  def auth = {
    "Basic " + Base64.encodeString("test:test1")
  }
}

case class BtcRequest(method: String, params: List[Any])

