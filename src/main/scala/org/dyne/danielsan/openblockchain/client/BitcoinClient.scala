package org.dyne.danielsan.openblockchain.client

import org.dyne.danielsan.openblockchain.data.entity.{Block, Transaction}
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.json4s.{DefaultFormats, _}

import scala.language.postfixOps
import scalaj.http.{Base64, Http}

/**
  * Created by dan_mi_sun on 10/03/2016.
  *
  * bitcoin-cli getblockhash 1
  * bitcoin-cli getblock 00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048
  * bitcoin-cli getrawtransaction 0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098
  * bitcoin-cli decoderawtransaction 01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff0
  * 704ffff001d0104ffffffff0100f2052a0100000043410496b538e853519c726a2c91e61ec11600ae1390813a627c66fb8be7947be63c52da758
  * 9379515d4e0a604f8141781e62294721166bf621e73a82cbf2342c858eeac00000000
  */

class BitcoinClient {

  implicit val formats = DefaultFormats

  val bitcoinServerUrl = sys.env.getOrElse("BITCOIN_SERVER_URL", "http://127.0.0.1:8332")
  val bitcoinAuth = "Basic " + Base64.encodeString(sys.env.getOrElse("BITCOIN_AUTH", "test:test1"))

  def getTransactions(block: Block): List[Transaction] = {
    try {
      block.tx.map(tx => getRequestResultAs[Transaction]("getrawtransaction", List(tx, 1)))
    } catch {
      case x: NullPointerException =>
        println("NPE!!!", block)
        List[Transaction]()
    }
  }

  def getBlock(id: Int): Block = {
    val hash = getBlockHash(id)
    val blockString = getBlockString(hash)
    val json = parse(blockString) \ "result"
    json.extract[Block]
  }

  private def getBlockHash(id: Int): String = {
    getRequestResultAs[String]("getblockhash", List(id))
  }

  private def getBlockString(hash: String): String = {
    getRequestBody("getblock", List(hash))
  }

  private def getRequestBody(method: String, params: List[Any]): String = {
    val request = BtcRequest(method, params)
    val json = write(request)
    Http(bitcoinServerUrl).postData(json)
      .header("content-type", "application/json")
      .header("Authorization", bitcoinAuth)
      .timeout(connTimeoutMs = 5000, readTimeoutMs = 15000)
      .asString
      .body
  }

  private def getRequestResultAs[T](method: String, params: List[Any])(implicit mf: Manifest[T]): T = {
    val resp = getRequestBody(method, params)
    (parse(resp) \ "result").extract[T]
  }

}

case class BtcRequest(method: String, params: List[Any])
