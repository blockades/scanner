package org.dyne.danielsan.openblockchain.client

import org.dyne.danielsan.openblockchain.data.entity.{Block, Transaction}
import org.dyne.danielsan.openblockchain.data.model.BlockTransactionCounts
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.json4s.{DefaultFormats, _}

import scala.language.postfixOps
import scalaj.http.{Base64, Http}

/**
  * Created by dan_mi_sun on 10/03/2016.
  *
  *  docker exec -it bitcoind bitcoin-cli -conf=/etc/bitcoin.conf getblockhash 1
  *  docker exec -it bitcoind bitcoin-cli -conf=/etc/bitcoin.conf getblock 00000000839a8e6886ab5951d76f411475428afc90947
  *                                                                        ee320161bbf18eb6048
  *  docker exec -it bitcoind bitcoin-cli -conf=/etc/bitcoin.conf getrawtransaction 0e3e2357e806b6cdb1f70b54c3a3a17b6714
  *                                                                                 ee1f0e68bebb44a74b1efd512098
  *  docker exec -it bitcoind bitcoin-cli -conf=/etc/bitcoin.conf decoderawtransaction 010000000100000000000000000000000
  *                                                                                    000000000000000000000000000000000
  *                                                                                    00000000ffffffff0704ffff001d0104f
  *                                                                                    fffffff0100f2052a0100000043410496
  *                                                                                    b538e853519c726a2c91e61ec11600ae1
  *                                                                                    390813a627c66fb8be7947be63c52da75
  *                                                                                    89379515d4e0a604f8141781e62294721
  *                                                                                    166bf621e73a82cbf2342c858eeac0000
  *                                                                                    0000
  *
  */

class BitcoinClient {

  implicit val formats = DefaultFormats

  val baseUrl = "http://127.0.0.1:8332"

  def decodeRawTransaction(id: Int): List[Transaction] = {
      val rawTxs = getRawTransaction(id)
      rawTxs.map( rawTx => (parse(Http(baseUrl).postData(write(BtcRequest("decoderawtransaction", List(rawTx))))
        .header("content-type", "application/json")
        .header("Authorization", auth)
        .asString
        .body) \ "result").extract[Transaction])
  }

  def getRawTransaction(id: Int): List[String] = {
    val txIds = extractTransactionIds(id)
    //here we have the list of txIds. We want to iterate through the entire list and do the rest of the commands
    txIds.map( txId => (parse(Http(baseUrl).postData(write(BtcRequest("getrawtransaction", List(txId))))
      .header("content-type", "application/json")
          .header("Authorization", auth)
          .asString
          .body) \ "result").extract[String])
  }

  def extractTransactionIds(id: Int): List[String] = {
    getTransactionIdsFromWithinBlock(id)
  }

  def getTransactionIdsFromWithinBlock(id: Int): List[String] = {
    val block = getBlockForId(id)
    block.tx
  }

  def getTransactionCountFromWithinBlock(id: Int): String = {
    val block = getBlockForId(id)
    val json = "blockTransactionCount" ->
                  ("hash" -> block.hash) ~
                  ("height" -> block.height) ~
                  ("num_transactions" -> block.tx.length)
    compact(render(json))
  }

  def updateBlockTransactionCount(id: Int): BlockTransactionCounts = {
    val counts = getTransactionCountFromWithinBlock(id)
    val btc = parse(counts) \ "blockTransactionCount"
    btc.extract[BlockTransactionCounts]
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

