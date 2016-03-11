package org.dyne.danielsan.superchain.client

import org.json4s._
import org.json4s.jackson.JsonMethods._


import sys.process._

/**
  * Created by dan_mi_sun on 10/03/2016.
  */
class BitcoinClient {

  implicit val formats = DefaultFormats

  def getHashForId(id: Int): String = {
    (s"bitcoin-cli getblockhash $id" !!).trim
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
}


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