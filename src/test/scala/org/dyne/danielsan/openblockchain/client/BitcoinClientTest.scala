package org.dyne.danielsan.openblockchain.client

import org.dyne.danielsan.openblockchain.UnitTest
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, _}

/**
  * Created by dan_mi_sun on 10/03/2016.
  */
class BitcoinClientTest extends UnitTest {

  implicit val formats = DefaultFormats

  describe("A BitcoinClient") {

    val client = new BitcoinClient


    it("should return the hash of a block by id") {
//      val id = 1
//      val expectedHash = "00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048"
//      client.getBlockHashForId(id) shouldEqual expectedHash
    }

//    it("should return a block by hash") {
//      val hash = "00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048"
//      val expectedJson =
//        """
//          |{
//          |    "hash" : "00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048",
//          |    "confirmations" : 0,
//          |    "size" : 215,
//          |    "height" : 1,
//          |    "version" : 1,
//          |    "merkleroot" : "0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098",
//          |    "tx" : [
//          |        "0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098"
//          |    ],
//          |    "time" : 1231469665,
//          |    "nonce" : 2573394689,
//          |    "bits" : "1d00ffff",
//          |    "difficulty" : 1.00000000,
//          |    "chainwork" : "0000000000000000000000000000000000000000000000000000000200020002",
//          |    "previousblockhash" : "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",
//          |    "nextblockhash" : "000000006a625f06636b8bb6ac7b960a8d03705d1ace08b1a19da3fdcc99ddbd"
//          |}
//          | """.stripMargin
//
//      val blockString = client.getBlockStringForHash(hash)
//      val mergeResetJson = parse("""{ "result": { "confirmations": 0 } }""")
//      val blockJson = parse(blockString).merge(mergeResetJson) // reset confirmations to 0
//      val block = blockJson \ "result"
//      val expectedBlock = parse(expectedJson)
//
//      block shouldEqual expectedBlock
//    }

    it("should ... chain of blocks")(pending)
    // the difficulty here is that we don't want to set the whole thing in motion
    // how best to mock out the response to one we expect?

  }

}
